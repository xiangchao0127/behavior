package com.handge.bigdata.resource.service.impl.monitor;

import com.handge.bigdata.UnifiedException;
import com.handge.bigdata.dao.ProxyFactory;
import com.handge.bigdata.dao.SQLBuilder;
import com.handge.bigdata.dao.api.IBaseDAO;
import com.handge.bigdata.dao.proxy.EsProxy;
import com.handge.bigdata.dao.proxy.MySQLProxy;
import com.handge.bigdata.enumeration.DAOProxyEnum;
import com.handge.bigdata.enumeration.ESIndexEnum;
import com.handge.bigdata.enumeration.ExceptionWrapperEnum;
import com.handge.bigdata.resource.models.request.monitor.NonWorkingAppTimeDetailParam;
import com.handge.bigdata.resource.models.request.monitor.TopOfNonWorkingAppTimeParam;
import com.handge.bigdata.resource.models.response.monitor.NonWorkingAppTimeDetail;
import com.handge.bigdata.resource.models.response.monitor.NonWorkingAppTimeTop;
import com.handge.bigdata.resource.service.api.monitor.ITopOfNonWorkingAppTime;
import com.handge.bigdata.utils.CollectionUtils;
import com.handge.bigdata.utils.FormulaUtil;
import com.handge.bigdata.utils.StringUtils;
import com.handge.bigdata.utils.TimeUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author liuqian
 * @date 2018/7/13
 * @Description:
 */
@Component
public class TopOfNonWorkingAppTimeImpl implements ITopOfNonWorkingAppTime {
    /**
     * 基础数据库查询bean
     */
    @Autowired
    private IBaseDAO baseDAO;

    @Override
    public Object listTopOfNonWorkingAppTime(TopOfNonWorkingAppTimeParam topOfNonWorkingAppTimeParam) {
        List<NonWorkingAppTimeTop> result = getResults(topOfNonWorkingAppTimeParam);
        return (topOfNonWorkingAppTimeParam.getN() <= result.size() ? result.subList(0, topOfNonWorkingAppTimeParam.getN()) : result);
    }

    @Override
    public Object listNonWorkingAppTimeDetail(NonWorkingAppTimeDetailParam nonWorkingAppTimeDetailParam) {
        List<NonWorkingAppTimeDetail> result = getResults(nonWorkingAppTimeDetailParam);
        //返回分页数据
        return CollectionUtils.getPageResult(result, nonWorkingAppTimeDetailParam.getPageNo(), nonWorkingAppTimeDetailParam.getPageSize());
    }

    /**
     * 查询ES
     * @return SearchResponse
     */
    private SearchResponse searchForEs(Object object){
        //今天零点零分零秒的时间戳
        long zeroTimeStamp = TimeUtils.getStartTime();
        //获取当前系统时间戳
        long curTimeStamp = TimeUtils.getNowTime();
        //工作无关标签
        List<String> nonWorkingTagList = baseDAO.listTagsOfNonWorking();
        //获取配置
        Map<String, Object> configParamMap = baseDAO.getConfigParam();

        EsProxy esProxy = ProxyFactory.createProxy(DAOProxyEnum.ES);
        String[] indices = esProxy.generateIndices(new Date(), new Date(), ESIndexEnum.MAPPING);
        TransportClient esClient = esProxy.getClient();
        String esType = configParamMap.get("ES_TYPE").toString();
        //非工作性上网sourceIP、count
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .filter(QueryBuilders
                        .rangeQuery("startTime")
                        .from(zeroTimeStamp, true)
                        .to(curTimeStamp, true)
                )
                .filter(QueryBuilders.termsQuery("appTags", nonWorkingTagList));
        if(object instanceof TopOfNonWorkingAppTimeParam ) {
            String departmentName = ((TopOfNonWorkingAppTimeParam)object).getDepartment();
            if (StringUtils.notEmpty(departmentName)) {
                if (baseDAO.getEmployeeIps(departmentName).size() == 0) {
                    throw new UnifiedException("部门名称不存在 ", ExceptionWrapperEnum.IllegalArgumentException);
                }
                queryBuilder.filter(QueryBuilders.termsQuery("localIp", baseDAO.getEmployeeIps(departmentName).get(departmentName)));
            }
        }
        TermsAggregationBuilder appAgg = AggregationBuilders.terms("appCount").field("appName").size(1000);
        SearchRequestBuilder srb = esClient.prepareSearch(indices)
                .setTypes(esType)
                .setSearchType(SearchType.QUERY_AND_FETCH)
                .setQuery(queryBuilder)
                .addAggregation(appAgg);
        //返回连接
        esProxy.returnClient();
        return esProxy.action(srb);
    }

    /**
     * 查询Mysql
     * @return
     */
    private Map<String,String> searchForMysql(){
        Map<String,String> appNameAndTagMap = new HashMap<>();
        MySQLProxy mySqlProxy = ProxyFactory.createProxy(DAOProxyEnum.MySQL);
        String sql = "select app_name,CONCAT(basic_class)\n" +
                     "FROM tag_url\n" +
                     "group by app_name ";
        String mySql1 = SQLBuilder.sql(sql).toString();
        ResultSet rsMySql = mySqlProxy.queryBySQL(mySql1);

        try {
            while (rsMySql.next()){
                String appName = rsMySql.getString(1);
                String basicClass = rsMySql.getString(2);
                appNameAndTagMap.put(appName,basicClass);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appNameAndTagMap;
    }

    private List getResults(Object object) {
        TreeMap<String, Long> appNameAndCountMap = new TreeMap<>();
        Map<String, Object> configParamMap = baseDAO.getConfigParam();
        double MINUTE_BY_ONE_CLICK = Double.parseDouble(String.valueOf(configParamMap.get("MINUTE_BY_ONE_CLICK")));
        SearchResponse response = searchForEs(object);
        Terms terms = response.getAggregations().get("appCount");
        Iterator<Terms.Bucket> iterator = terms.getBuckets().iterator();
        while (iterator.hasNext()) {
            Terms.Bucket bucket = iterator.next();
            String appName = bucket.getKeyAsString();
            long appNameCount = bucket.getDocCount();
            //将appName为""的情况排除
            if (StringUtils.notEmpty(appName)) {
                appNameAndCountMap.put(appName, appNameCount);
            }
        }
        //将appName统计次数按降序排列
        List<Map.Entry<String, Long>> list = new ArrayList<Map.Entry<String, Long>>(appNameAndCountMap.entrySet());
        // 通过比较器来实现排序
        list.sort(new Comparator<Map.Entry<String, Long>>() {
            @Override
            public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                // 降序排序
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        if (object instanceof TopOfNonWorkingAppTimeParam) {
            List<NonWorkingAppTimeTop> appTime = new ArrayList<>();
            for (Map.Entry<String, Long> entry : list) {
                NonWorkingAppTimeTop nonWorkingAppTimeTop = new NonWorkingAppTimeTop();
                nonWorkingAppTimeTop.setAppName(entry.getKey());
                nonWorkingAppTimeTop.setTimes(FormulaUtil.timesToHourMinute(entry.getValue(), String.valueOf(MINUTE_BY_ONE_CLICK)));
                appTime.add(nonWorkingAppTimeTop);
            }
            return appTime;
        }else{
            List<NonWorkingAppTimeDetail> appTime = new ArrayList<>();
            Map<String, String> appNameAndTagMap = searchForMysql();
            for (Map.Entry<String, Long> entry : list) {
                NonWorkingAppTimeDetail nonWorkingAppTimeDetail = new NonWorkingAppTimeDetail();
                nonWorkingAppTimeDetail.setAppName(entry.getKey());
                nonWorkingAppTimeDetail.setTimes(FormulaUtil.timesToHourMinute(entry.getValue(), String.valueOf(MINUTE_BY_ONE_CLICK)));
                nonWorkingAppTimeDetail.setBasicTag(appNameAndTagMap.get(entry.getKey()));
                appTime.add(nonWorkingAppTimeDetail);
            }
            return appTime;
        }
    }
}
