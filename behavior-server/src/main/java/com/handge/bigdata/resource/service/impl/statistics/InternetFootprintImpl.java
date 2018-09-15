package com.handge.bigdata.resource.service.impl.statistics;

import com.handge.bigdata.UnifiedException;
import com.handge.bigdata.dao.ProxyFactory;
import com.handge.bigdata.dao.SQLBuilder;
import com.handge.bigdata.dao.api.IBaseDAO;
import com.handge.bigdata.dao.proxy.EsProxy;
import com.handge.bigdata.dao.proxy.Proxy;
import com.handge.bigdata.enumeration.DAOProxyEnum;
import com.handge.bigdata.enumeration.DateFormatEnum;
import com.handge.bigdata.enumeration.ESIndexEnum;
import com.handge.bigdata.enumeration.JobPropertyEnum;
import com.handge.bigdata.resource.models.request.statistics.FootPrintParam;
import com.handge.bigdata.resource.models.response.statistics.InternetFootprint;
import com.handge.bigdata.resource.models.response.statistics.InternetFootprintInfo;
import com.handge.bigdata.resource.service.api.statistics.IInternetFootprint;
import com.handge.bigdata.utils.DateUtil;
import com.handge.bigdata.utils.PageResults;
import com.handge.bigdata.utils.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;

/**
 * @author MaJianfu
 * @date 2018/5/18 16:36
 **/
@SuppressWarnings("Duplicates")
@Component
public class InternetFootprintImpl implements IInternetFootprint {
    /**
     * 基础数据库查询Bean
     */
    @Autowired
    IBaseDAO baseDao;

    List<String> tagNameList = new ArrayList<>();
    List<String> ipList = new ArrayList<>();

    @Override
    public Object listInternetFootprint(FootPrintParam footPrintParam) {
        PageResults<InternetFootprint> internetFootprintPageResults = pageResult(footPrintParam,
                footPrintParam.getPageNo(),
                footPrintParam.getPageSize());
        return internetFootprintPageResults;
    }

    Proxy proxyMysql = ProxyFactory.createProxy(DAOProxyEnum.MySQL);
    private PageResults<InternetFootprint> pageResult(FootPrintParam footPrintParam, int pageNo, int pageSize) {

        List<InternetFootprint> result = new ArrayList<>();
        Map<String, String> internetFootprintMap = new LinkedHashMap<>();
        EsProxy esProxy = ProxyFactory.createProxy(DAOProxyEnum.ES);
        String[] ES_INDEX = esProxy.generateIndices(
                StringUtils.notEmpty(footPrintParam.getStartTime()) ? footPrintParam.getStartTime() : new Date(),
                StringUtils.notEmpty(footPrintParam.getEndTime()) ? footPrintParam.getEndTime() : new Date(),
                ESIndexEnum.MAPPING
        );

        TransportClient client = esProxy.getClient();
            if (StringUtils.isEmpty(footPrintParam.getProperty())) {
                footPrintParam.setProperty("全部");
            }
            String propertySql = "SELECT tag_name AS tagName FROM tag_property WHERE property=#{property}";
            String excuteSql1 = "";
            if ("全部".equals(footPrintParam.getProperty())) {
                excuteSql1 = "SELECT tag_name AS tagName FROM tag_property";
            } else {
                excuteSql1 = SQLBuilder.sql(propertySql)
                        .setParamter("property", footPrintParam.getProperty())
                        .toString();
            }
            ResultSet rs = (ResultSet) proxyMysql.queryBySQL(excuteSql1);
            if (JobPropertyEnum.不确定.getCode().equals(footPrintParam.getProperty())) {
                tagNameList.add("");
            }
        try {
            while (rs.next()) {
                tagNameList.add(rs.getString(1));
            }
        } catch (SQLException e) {
            throw new UnifiedException(e);
        }
        String department = "";
            String name = "";
            String number = "";
            String ip = "";
            String type = "";
            String id = "";
            String accessTime = "";
            String app = "";
            String url = "";
            String property = "";
            String mySql = "SELECT dep.department_name AS department_name,dev.static_ip AS static_ip,emp.name AS name,emp.number AS number\n" +
                    "FROM entity_employee_information_basic emp \n" +
                    "JOIN auth_account cou ON cou.employee_id = emp.id\n" +
                    "JOIN entity_device_basic dev ON dev.account_id = cou.id\n" +
                    "INNER JOIN entity_department_information_basic AS dep\n" +
                    "WHERE emp.department_id = dep.department_id";
            if (StringUtils.notEmpty(footPrintParam.getName())) {
                String[] split = footPrintParam.getName().split("\\|", -1);
                if (split.length != 3) {
                    // TODO: 2018/5/31 参数异常
                    return null;
                } else {
                    if (StringUtils.notEmpty(split[0])) {
                        mySql += " and emp.name='" + split[0] + "'";
                    }
                    if (StringUtils.notEmpty(split[1])) {
                        mySql += " and dep.department_name='" + split[1] + "'";
                    }
                    if (StringUtils.notEmpty(split[2])) {
                        mySql += " and emp.number='" + split[2] + "'";
                    }
                }
            }
            if (StringUtils.notEmpty(footPrintParam.getIp()) && !footPrintParam.getIp().equals("全部")) {
                mySql += " and dev.static_ip='" + footPrintParam.getIp() + "'";
            }
            ResultSet resultSet = (ResultSet) proxyMysql.queryBySQL(mySql);
        try {
            while (resultSet.next()) {
                department = resultSet.getString(1);
                name = resultSet.getString(3);
                number = resultSet.getString(4);
                ip = resultSet.getString(2);
                ipList.add(ip);
            }
        } catch (SQLException e) {
            throw new UnifiedException(e);
        }
        int startNum = (pageNo - 1) * pageSize;
            int endSize = pageNo * pageSize <= 10000 ? pageSize : 10000 - startNum;
            QueryBuilder queryBuilder = createQuery(footPrintParam);
            Map<String, Object> configParamMap = baseDao.getConfigParam();
            String esType = configParamMap.get("ES_TYPE").toString();
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch(ES_INDEX).setTypes(esType)
                    .setQuery(queryBuilder).addSort("startTime", SortOrder.DESC).setFrom(startNum).setSize(endSize);
            SearchResponse response = esProxy.action(searchRequestBuilder);
            SearchHits hits = response.getHits();
            SearchHit[] searchHits = hits.hits();
            for (SearchHit searchHit : searchHits) {
                id = searchHit.getId().toString();
                app = searchHit.getSource().get("appName").toString();
                url = searchHit.getSource().get("domain").toString();
                accessTime = searchHit.getSource().get("startTime").toString();
                ip = searchHit.getSource().get("localIp").toString();
                type = searchHit.getSource().get("appTags").toString();
                String style = type.replace("[,", "[[],");
                property = propertyQuery(type);
                internetFootprintMap.put(id, ip + "|" + style + "|" + accessTime + "|" + app + "|" + url + "|" + property);
            }
            esProxy.returnClient();
            List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>
                    (internetFootprintMap.entrySet());
            // 通过比较器来实现排序
            Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
                @Override
                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    return -Long.compare(Long.parseLong(o1.getValue().split("\\|")[2]),
                            Long.parseLong(o2.getValue().split("\\|")[2]));
                }
            });
            List<InternetFootprintInfo> infoList = new ArrayList<>();
            for (Map.Entry<String, String> mapping : list) {
                InternetFootprintInfo internetFootprintInfo = new InternetFootprintInfo();
                String value = mapping.getValue();
                String[] str = value.split("\\|", -1);
                internetFootprintInfo.setIp(str[0]);
                internetFootprintInfo.setType(str[1]);
                internetFootprintInfo.setAccessTime(DateUtil.timeStampToStrDate(Long.valueOf(str[2]), DateFormatEnum.SECONDS));
                internetFootprintInfo.setApp(str[3]);
                internetFootprintInfo.setUrl(str[4]);
                internetFootprintInfo.setProperty(str[5]);
                infoList.add(internetFootprintInfo);
            }

            InternetFootprint internetFootprint = new InternetFootprint();
            internetFootprint.setDepartment(department);
            internetFootprint.setName(name);
            internetFootprint.setNumber(number);
            internetFootprint.setInfoList(infoList);
            tagNameList.clear();
            ipList.clear();
            if (!"".equals(internetFootprint.getNumber())) {
                result.add(internetFootprint);
            }
            PageResults<InternetFootprint> pageResult = new PageResults<>();
            pageResult.setCurrentPage(pageNo);
            pageResult.setPageSize(pageSize);
            pageResult.setTotalCount((int) hits.getTotalHits() > 10000 ? 10000 : (int) hits.getTotalHits());
            pageResult.setPageCount(((pageResult.getTotalCount() - 1) / pageSize) + 1);
            pageResult.setNextPageNo(pageNo + 1 < pageResult.getPageCount() ? pageNo + 1 : pageResult.getPageCount());
            pageResult.setResults(result);
            return pageResult;
    }

    private BoolQueryBuilder createQuery(FootPrintParam footPrintParam) {
            BoolQueryBuilder query = QueryBuilders.boolQuery();
            // 如果ip存在，只要符合一种就查询
            query.filter(QueryBuilders.termsQuery("localIp", ipList));
            //appName
            if (StringUtils.notEmpty(footPrintParam.getApp())) {
                query.filter(new QueryStringQueryBuilder(footPrintParam.getApp()).field("appName"));
            }
            //Type
            if (StringUtils.notEmpty(footPrintParam.getType())) {
                query.filter(new QueryStringQueryBuilder(footPrintParam.getType()).field("appTags"));
            }
            //Property
            if (StringUtils.notEmpty(footPrintParam.getProperty())) {
                query.filter(QueryBuilders.termsQuery("appTags", tagNameList));
            }
            //StartTime
            if (StringUtils.notEmpty(footPrintParam.getStartTime())) {
                long begin = 0;
                try {
                    begin = DateUtil.dateToTimeStamp(footPrintParam.getStartTime(), DateFormatEnum.SECONDS);
                } catch (ParseException e) {
                    throw new UnifiedException(e);
                }
                query.filter(new RangeQueryBuilder("startTime").format("epoch_millis").gte(begin));
            }
            //EndTime
            if (StringUtils.notEmpty(footPrintParam.getEndTime())) {
                long end = 0;
                try {
                    end = DateUtil.dateToTimeStamp(footPrintParam.getEndTime(), DateFormatEnum.SECONDS);
                } catch (ParseException e) {
                    throw new UnifiedException(e);
                }
                query.filter(new RangeQueryBuilder("startTime").format("epoch_millis").lte(end));
            }
            return query;
    }

    private String propertyQuery(String type) {
        String property = "";
        String tags = "";
        Set<String> set = new HashSet<String>();
        HashMap<String, String> tagMap = tagMap();
        if (!type.equals("[]")) {
            String replace = type.replace("[", "").replace("]", "").replace(" ", "");
            if (!replace.contains(",")) {
                if(tagMap.containsKey(replace)){
                    tags = tagMap.get(replace);
                    if (tags.equals(JobPropertyEnum.工作相关.getCode())) {
                        set.add("工作相关");
                    } else if (tags.equals(JobPropertyEnum.工作无关.getCode())) {
                        set.add("工作无关");
                    } else {
                        set.add("不确定");
                    }
                }
            } else {
                String[] arr = replace.split(",");
                for (String array : arr) {
                    if ("".equals(array)) {
                        set.add("不确定");
                    } else {
                        if(tagMap.containsKey(array)){
                            tags = tagMap.get(array);
                            if (tags.equals(JobPropertyEnum.工作相关.getCode())) {
                                set.add("工作相关");
                            } else if (tags.equals(JobPropertyEnum.工作无关.getCode())) {
                                set.add("工作无关");
                            } else {
                                set.add("不确定");
                            }
                        }
                    }
                }
            }
        } else {
            set.add("不确定");
        }
        String[] strings = set.toArray(new String[set.size()]);
        property = Arrays.asList(strings).toString();
        return property;
    }

    private HashMap<String,String> tagMap(){
        HashMap<String,String> tagMap=new HashMap<>();
        String sqlTag ="select * from tag_property";
        ResultSet resultSet = (ResultSet) proxyMysql.queryBySQL(sqlTag);
        try {
            while (resultSet.next()){
                String tag_name = resultSet.getString("tag_name");
                String property = resultSet.getString("property");
                tagMap.put(tag_name,property);
            }
            return tagMap;
        } catch (SQLException e) {
            throw new UnifiedException(e);
        }
    }

}
