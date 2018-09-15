package com.handge.bigdata.resource.service.impl.monitor;

import com.handge.bigdata.dao.ProxyFactory;
import com.handge.bigdata.dao.api.IBaseDAO;
import com.handge.bigdata.dao.proxy.EsProxy;
import com.handge.bigdata.enumeration.DAOProxyEnum;
import com.handge.bigdata.enumeration.ESIndexEnum;
import com.handge.bigdata.resource.models.request.monitor.NonWorkingAppDetailParam;
import com.handge.bigdata.resource.models.request.monitor.TopOfNonWorkingAppParam;
import com.handge.bigdata.resource.models.response.monitor.NonWorkingAppTop;
import com.handge.bigdata.resource.service.api.monitor.ITopOfNonWorkingApp;
import com.handge.bigdata.utils.CollectionUtils;
import com.handge.bigdata.utils.DateUtil;
import com.handge.bigdata.utils.PageResults;
import com.handge.bigdata.utils.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.max.InternalMax;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by DaLu Guo on 2018/5/4.
 */
@Component
public class TopOfNonWorkingAppImpl implements ITopOfNonWorkingApp {

    /**
     * 基础数据库查询Bean
     */
    @Autowired
    IBaseDAO baseDAO;

    @Override
    public Object listTopOfNonWorkingApp(TopOfNonWorkingAppParam topOfNonWorkingAppParam) {
        /**
         * 1 查询ES，得到每个人在这一段时间内最新访问的应用，统计每个应用的人数
         */
        Map<String, Integer> appCountMap = searchAndCount("",topOfNonWorkingAppParam.getDepartment());
        /**
         * 2 封装结果并返回
         */
        List<NonWorkingAppTop> result = returnListResult(appCountMap,topOfNonWorkingAppParam.getDepartment());

        return result.subList(0, result.size() > topOfNonWorkingAppParam.getN() ? topOfNonWorkingAppParam.getN() : result.size());
    }

    @Override
    public Object listNonWorkingAppDetail(NonWorkingAppDetailParam nonWorkingAppDetailParam) {

        /**
         * 1 查询ES，得到每个人在这一段时间内最新访问的应用，统计每个应用的人数
         */
        Map<String, Integer> appCountMap = searchAndCount(nonWorkingAppDetailParam.getAppName(),nonWorkingAppDetailParam.getDepartment());
        /**
         * 2 结果转为list
         */
        List<NonWorkingAppTop> list = returnListResult(appCountMap,nonWorkingAppDetailParam.getDepartment());
        /**
         * 3 返回分页数据
         */
        PageResults<NonWorkingAppTop> results = returnPageResult(list, nonWorkingAppDetailParam.getPageNo(), nonWorkingAppDetailParam.getPageSize());

        return results;
    }


    private Map<String, Integer> searchAndCount(String appName,String department) {
        //从数据库获取配置
        Map<String, Object> configParamMap = baseDAO.getConfigParam();

        Map<String, Integer> appCountMap = new HashMap<>();
        List<String> tags = baseDAO.listTagsOfNonWorking();
        EsProxy esProxy = ProxyFactory.createProxy(DAOProxyEnum.ES);
        TransportClient esClient = esProxy.getClient();
//        //生效分钟
//        double MINUTE_BY_ONE_CLICK = Double.parseDouble(configParamMap.get("MINUTE_BY_ONE_CLICK").toString());
        //开始时间为当前时间- 生效分钟 * 2
        double queryTime = Double.parseDouble(configParamMap.get("QUERY_TIME").toString());
        Long startTimeMills = DateUtil.getTimeByMinute(-queryTime);
        Long nowTimeMills = System.currentTimeMillis();
        String[] indices = esProxy.generateIndices(new Date(), new Date(), ESIndexEnum.MAPPING);
        String esType = configParamMap.get("ES_TYPE").toString();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .mustNot(QueryBuilders.termsQuery("appName", ""))
                .filter(QueryBuilders.termsQuery("appTags", tags))
                .filter(QueryBuilders.rangeQuery("startTime").from(startTimeMills, true).to(nowTimeMills, true));
        if(StringUtils.notEmpty(department)){
            ArrayList<String> ips = baseDAO.getEmployeeIps(department).get(department);
            queryBuilder.filter(QueryBuilders.termsQuery("localIp", ips));
        }
        SearchRequestBuilder builder = esClient.prepareSearch(indices)
                .setTypes(esType)
                .setQuery(queryBuilder)
                .addAggregation(AggregationBuilders.terms("localIp").field("localIp").size(10000)
                        .subAggregation(AggregationBuilders.max("startTime").field("startTime")).size(10000)).setSize(10000);
        SearchResponse sr = esProxy.action(builder);
        Terms localIp = sr.getAggregations().get("localIp");
        HashMap<String, String> ipNumberMap = baseDAO.getAllEmployeeIpAndNumber();
        HashMap<String, String> numberIpMap = CollectionUtils.hashMapInterchange(ipNumberMap);
        for (Terms.Bucket bucket : localIp.getBuckets()) {
            String ip = bucket.getKeyAsString();
            double latestTime = ((InternalMax) bucket.getAggregations().get("startTime")).getValue();
            if (numberIpMap.containsValue(ip)) {
                Object appNameBuilder = null;
                if (StringUtils.notEmpty(appName)) {
                    appNameBuilder = QueryBuilders.wildcardQuery("appName","*"+appName+"*");
                } else {
                    appNameBuilder = QueryBuilders.scriptQuery(new Script("1 == 1"));
                }
                SearchRequestBuilder requestBuilder = esClient.prepareSearch(indices)
                        .setQuery(QueryBuilders.boolQuery()
                                .mustNot(QueryBuilders.termsQuery("appName", ""))
                                .filter(QueryBuilders.termsQuery("appTags", tags))
                                .filter(QueryBuilders.termQuery("localIp", ip))
                                .filter(QueryBuilders.termQuery("startTime", new BigDecimal(latestTime).toString()))
                                .filter((QueryBuilder) appNameBuilder)
                        )
                        .addAggregation(AggregationBuilders.terms("appName").field("appName").size(10000)
                                .subAggregation(AggregationBuilders.count("count").field("_index")
                                )
                        );
                SearchResponse response = esProxy.action(requestBuilder);
                Terms appTerm = response.getAggregations().get("appName");
                for (Terms.Bucket bucket1 : appTerm.getBuckets()) {
                    String app = bucket1.getKeyAsString();
                    if (appCountMap.keySet().contains(app)) {
                        appCountMap.put(app, appCountMap.get(app) + 1);
                    } else {
                        appCountMap.put(app, 1);
                    }
                }
            }
        }
        //这里将map.entrySet()转换成list
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(appCountMap.entrySet());
        //然后通过比较器来实现排序
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            //降序
            @Override
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        Map<String, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> map : list) {
            result.put(map.getKey(), map.getValue());
        }
        esProxy.returnClient();
        return result;
    }

    private List<NonWorkingAppTop> returnListResult(Map<String, Integer> appCountMap,String department) {
        List<NonWorkingAppTop> result = new ArrayList<>();
        int totalNum = baseDAO.totalNumberOfEmployeesOnGuard();
        if(StringUtils.notEmpty(department)){
            totalNum = baseDAO.numberOfEmployeesGroupByDep().get(department);
        }
        for (String app : appCountMap.keySet()) {
            NonWorkingAppTop nonWorkingAppTop = new NonWorkingAppTop();
            nonWorkingAppTop.setAppName(app);
            nonWorkingAppTop.setNumOfVisit(String.valueOf(appCountMap.get(app)));
            nonWorkingAppTop.setNumOfAllPeople(String.valueOf(totalNum));
            nonWorkingAppTop.setRatioOfNonWorkingApp(new BigDecimal(appCountMap.get(app))
                    .divide(new BigDecimal(totalNum), 3, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal(100))
                    .setScale(1, BigDecimal.ROUND_HALF_UP)
                    .toPlainString());
            result.add(nonWorkingAppTop);
        }
        return result;
    }

    private PageResults<NonWorkingAppTop> returnPageResult(List<NonWorkingAppTop> results, int pageNo, int pageSize) {
        PageResults<NonWorkingAppTop> pageResults = CollectionUtils.getPageResult(results, pageNo, pageSize);
        return pageResults;
    }
}
