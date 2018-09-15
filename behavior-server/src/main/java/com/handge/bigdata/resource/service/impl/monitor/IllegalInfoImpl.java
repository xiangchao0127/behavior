package com.handge.bigdata.resource.service.impl.monitor;

import com.handge.bigdata.UnifiedException;
import com.handge.bigdata.dao.ProxyFactory;
import com.handge.bigdata.dao.SQLBuilder;
import com.handge.bigdata.dao.api.IBaseDAO;
import com.handge.bigdata.dao.api.impl.BaseDAO;
import com.handge.bigdata.dao.proxy.EsProxy;
import com.handge.bigdata.dao.proxy.Proxy;
import com.handge.bigdata.enumeration.DAOProxyEnum;
import com.handge.bigdata.enumeration.DateFormatEnum;
import com.handge.bigdata.enumeration.ESIndexEnum;
import com.handge.bigdata.resource.models.request.monitor.AlarmInfoDetailParam;
import com.handge.bigdata.resource.models.request.monitor.IllegalDetailParam;
import com.handge.bigdata.resource.models.request.monitor.IllegalInfoParam;
import com.handge.bigdata.resource.models.response.monitor.AbnormalAlarmInfo;
import com.handge.bigdata.resource.models.response.monitor.Illegal;
import com.handge.bigdata.resource.models.response.monitor.IllegalInfo;
import com.handge.bigdata.resource.models.response.monitor.IllegalInfoV2;
import com.handge.bigdata.resource.service.api.monitor.IIllegalInfo;
import com.handge.bigdata.utils.CollectionUtils;
import com.handge.bigdata.utils.DateUtil;
import com.handge.bigdata.utils.PageResults;
import com.handge.bigdata.utils.StringUtils;
import com.sun.org.apache.bcel.internal.generic.ARRAYLENGTH;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.text.ParseException;
import java.util.*;

/**
 * Created by DaLu Guo on 2018/5/16.
 */
@Component
public class IllegalInfoImpl implements IIllegalInfo {
    @Autowired
    IBaseDAO baseDAO;
    /**
     * 直接查询es
     * @param illegalInfoParam
     * @return
     */
    @Override
    public Object listIllegalInfo(IllegalInfoParam illegalInfoParam) {
        Map<String, Object> configMap = baseDAO.getConfigParam();
        List<String> illegalTags = Arrays.asList(configMap.get("ILLEGAL_TAGS").toString().split(","));
        //region 1 查詢es
        EsProxy esProxy = ProxyFactory.createProxy(DAOProxyEnum.ES);
        TransportClient esClient = esProxy.getClient();
        String today = DateUtil.timeStampToStrDate(System.currentTimeMillis(), DateFormatEnum.DAY);
        String[] indices = esProxy.generateIndices(today, today, ESIndexEnum.MAPPING);
        String esType = configMap.get("ES_TYPE").toString();
        long startTimeStamp = 0;
        try {
            startTimeStamp = DateUtil.dateToStartOrEndStamp(DateUtil.timeStampToStrDate(System.currentTimeMillis(), DateFormatEnum.SECONDS), 0);
        } catch (ParseException e) {
            throw new UnifiedException(e);
        }
        long endTimeStamp = System.currentTimeMillis();
        List<String> ips = new ArrayList<>();
        if (StringUtils.notEmpty(illegalInfoParam.getDepartment())) {
            ips = baseDAO.getEmployeeIps(illegalInfoParam.getDepartment()).get(illegalInfoParam.getDepartment());
        } else {
            for (String key : baseDAO.getEmployeeIps("").keySet()) {
                ips.addAll(baseDAO.getEmployeeIps("").get(key));
            }
        }
        if (ips == null) {
            return new IllegalInfo();
        }
        SearchRequestBuilder builder = esClient.prepareSearch(indices)
                .setTypes(esType)
                .setQuery(QueryBuilders.boolQuery()
                        .filter(QueryBuilders.rangeQuery("startTime").from(startTimeStamp, true).to(endTimeStamp, true))
                        .filter(QueryBuilders.termsQuery("appTags", illegalTags))
                        .filter(QueryBuilders.termsQuery("localIp", ips))
                )
                .addAggregation(AggregationBuilders.terms("appName").field("appName").size(illegalInfoParam.getN())
                        .subAggregation(AggregationBuilders.terms("localIp").field("localIp").size(10000)));
        SearchResponse response = esProxy.action(builder);
        esProxy.returnClient();
        //endregion
        return wrapData(response);
    }

    @Override
    public Object listIllegalInfoDetail(IllegalDetailParam illegalDetailParam) {
        Map<String, Object> configMap = baseDAO.getConfigParam();
        List<String> illegalTags = Arrays.asList(configMap.get("ILLEGAL_TAGS").toString().split(","));
        //region 1 查詢es
        EsProxy esProxy = ProxyFactory.createProxy(DAOProxyEnum.ES);
        TransportClient esClient = esProxy.getClient();

        String esType = configMap.get("ES_TYPE").toString();
        long startTimeStamp = 0;
        try {
            startTimeStamp = DateUtil.dateToTimeStamp(illegalDetailParam.getStartTime(), DateFormatEnum.SECONDS);
        } catch (ParseException e) {
            throw new UnifiedException(e);
        }
        long endTimeStamp = 0;
        try {
            endTimeStamp = DateUtil.dateToTimeStamp(illegalDetailParam.getEndTime(), DateFormatEnum.SECONDS);
        } catch (ParseException e) {
            throw new UnifiedException(e);
        }
        String[] indices = esProxy.generateIndices(startTimeStamp, endTimeStamp, ESIndexEnum.MAPPING);
        List<String> ips = getIps(illegalDetailParam.getDepartment(), illegalDetailParam.getName(), illegalDetailParam.getNumber());
        SearchRequestBuilder builder = esClient.prepareSearch(indices)
                .setTypes(esType)
                .setQuery(QueryBuilders.boolQuery()
                        .filter(QueryBuilders.rangeQuery("startTime").from(startTimeStamp, true).to(endTimeStamp, true))
                        .filter(QueryBuilders.termsQuery("appTags", illegalTags))
                        .filter(QueryBuilders.termsQuery("localIp", ips))
                )
                .addAggregation(AggregationBuilders.terms("appName").field("appName").size(10000)
                        .subAggregation(AggregationBuilders.terms("localIp").field("localIp").size(10000)));
        SearchResponse response = esProxy.action(builder);
        esProxy.returnClient();
        //endregion
        List<IllegalInfo> list = wrapData(response);
        return CollectionUtils.getPageResult(list,illegalDetailParam.getPageNo(),illegalDetailParam.getPageSize());
    }

    private List<IllegalInfo> wrapData( SearchResponse response){
        List<IllegalInfo> result = new ArrayList<>();
        //获取所有员工ip及对应的职工编号
        HashMap<String, String> allEmployeeIpAndNumber = baseDAO.getAllEmployeeIpAndNumber();
        Map<String, String> allEmployeeNumberAndName = baseDAO.getAllEmployeeNumberAndName();
        Terms appNameTerm =  response.getAggregations().get("appName");
        for(Terms.Bucket bucket : appNameTerm.getBuckets() ){
            String appName = bucket.getKeyAsString();
            String appTagCount = String.valueOf(bucket.getDocCount());
            Terms localIpTerm =  bucket.getAggregations().get("localIp");
            HashMap<String,Long> nameAndCountMap = new HashMap<>();
            for(Terms.Bucket ipBucket : localIpTerm.getBuckets()){
                String number = allEmployeeIpAndNumber.get(ipBucket.getKey());
                long count = ipBucket.getDocCount();
                if(nameAndCountMap.keySet().contains(number)){
                    long current = nameAndCountMap.get(number);
                    nameAndCountMap.put(number,current + count);
                }else{
                    nameAndCountMap.put(number,count);
                }
            }
            //map排序
            List<Map.Entry<String, Long>> list = new ArrayList<Map.Entry<String, Long>>(nameAndCountMap.entrySet());
            // 通过比较器来实现排序
            list.sort(new Comparator<Map.Entry<String, Long>>() {
                @Override
                public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                    // 降序排序
                    return o2.getValue().compareTo(o1.getValue());
                }
            });
            List<String> names = new ArrayList<>();
            for (Map.Entry<String, Long> mapping : list) {
                if(names.size() >= 3){
                    break;
                }else{
                    names.add(allEmployeeNumberAndName.get(mapping.getKey()));
                }
            }
            IllegalInfo illegalInfo = new IllegalInfo();
            illegalInfo.setAppName(appName);
            illegalInfo.setCount(appTagCount);
            illegalInfo.setName(names);
            result.add(illegalInfo);
        }
        return result;
    }

    private List<String> getIps(String department, String name, String number) {
        Proxy proxy = ProxyFactory.createProxy(DAOProxyEnum.MySQL);
        List<String> ips = new ArrayList<>();
        try {
            String sql = "\n" +
                    "SELECT\n" +
                    "\tdev.static_ip AS ip\n" +
                    "FROM\n" +
                    "\tentity_employee_information_basic emp\n" +
                    "JOIN auth_account cou ON cou.employee_id = emp.id\n" +
                    "JOIN entity_device_basic dev ON dev.account_id = cou.id\n" +
                    "INNER JOIN entity_department_information_basic AS dep ON emp.department_id = dep.department_id\n" +
                    "AND emp. STATUS != 4\n";
            if (StringUtils.notEmpty(department)) {
                sql += "AND dep.department_name LIKE #{department}\n";
            }
            if (StringUtils.notEmpty(name)) {
                sql += "AND emp.`name` LIKE #{name}\n";
            }
            if (StringUtils.notEmpty(number)) {
                sql += "AND emp.number = #{number};";
            }
            SQLBuilder excuteSqlBuilder = SQLBuilder.sql(sql);

            if (StringUtils.notEmpty(department)) {
                excuteSqlBuilder.setParamter("department", "%" + department + "%");
            }
            if (StringUtils.notEmpty(name)) {
                excuteSqlBuilder.setParamter("name", "%" + name + "%");
            }
            if (StringUtils.notEmpty(number)) {
                excuteSqlBuilder.setParamter("number", number);
            }
            String excuteSql = excuteSqlBuilder.toString();
            ResultSet tagResult = (ResultSet) proxy.queryBySQL(excuteSql);

            while (tagResult.next()) {
                ips.add(tagResult.getString(1));
            }
        } catch (Exception e) {
            throw new UnifiedException(e);
        }
        return ips;
    }




}
