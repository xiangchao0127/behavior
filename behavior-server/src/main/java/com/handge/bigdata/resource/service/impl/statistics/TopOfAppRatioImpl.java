package com.handge.bigdata.resource.service.impl.statistics;

import com.handge.bigdata.dao.ProxyFactory;
import com.handge.bigdata.dao.SQLBuilder;
import com.handge.bigdata.dao.api.IBaseDAO;
import com.handge.bigdata.dao.proxy.EsProxy;
import com.handge.bigdata.dao.proxy.Proxy;
import com.handge.bigdata.enumeration.DAOProxyEnum;
import com.handge.bigdata.resource.models.UserContext;
import com.handge.bigdata.resource.models.response.statistics.NonWorkingApp;
import com.handge.bigdata.resource.models.response.statistics.TagRatio;
import com.handge.bigdata.resource.service.api.statistics.ITopOfAppRatio;
import com.handge.bigdata.utils.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.text.ParseException;
import java.util.*;

/**
 * Created by DaLu Guo on 2018/5/4.
 */
@Component
public class TopOfAppRatioImpl implements ITopOfAppRatio {

    Log logger = LogFactory.getLog(this.getClass());

    /**
     * ES表名
     */
    private static String ES_INDEX_NAME = "newdataflow";
    /**
     * 每次点击等价分钟数
     */
    private static double MINUTE_BY_ONE_CLICK = 1;
    /**
     * 基础数据库查询Bean
     */
    @Autowired
    IBaseDAO baseDAO;


    @Deprecated
//    @Override
    public Object listTopOfAppRatio1(String startTime, String endTime, int n, int appNum, UserContext context, double threshold) {
        List<TagRatio> result = new ArrayList<>();
        //用于存储每个标签的人数
        HashMap<String, Integer> tagCountMap = new HashMap<>();
        Proxy proxy = ProxyFactory.createProxy(DAOProxyEnum.ES);
        try {
            String sql = "SELECT COUNT(*) AS size  FROM newdataflow \n" +
                    "WHERE \n" +
                    "startTime >= #{startTime} \n" +
//                    "AND startTime <= #{endTime} \n" +
                    "GROUP BY \n" +
                    "app_tag_collection.keyword,sourceIP \n" +
                    "ORDER BY size DESC";
            String excuteSql = SQLBuilder.sql(sql)
                    .setParamter("startTime", 1523035180000L)
//                    .setParamter("endTime",Long.parseLong(endTime))
                    .toString();
            ResultSet tagResult = (ResultSet) proxy.queryBySQL(excuteSql);
            logger.debug(excuteSql);
            while (tagResult.next()) {
                if (tagResult.getDouble("size") > threshold) {
                    String tagName = tagResult.getString("app_tag_collection.keyword");
                    if (!tagCountMap.keySet().contains(tagName)) {
                        tagCountMap.put(tagName, 1);
                    } else {
                        int tagCount = tagCountMap.get(tagName);
                        tagCountMap.put(tagName, tagCount + 1);
                    }
                }
            }
            Integer totalNum = 0;
            for (int num : tagCountMap.values()) {
                totalNum += num;
            }
            for (String tagName : tagCountMap.keySet()) {
                TagRatio tagRatio = new TagRatio();
                //标签人数
                tagRatio.setTagNum(tagCountMap.get(tagName).toString());
                //总人数
                tagRatio.setTotalNum(String.valueOf(totalNum));
                //标签名
                tagRatio.setTagName(tagName);
                //标签占比
//                tagRatio.setRatio(NumberUtil.decimalToPercentage(new BigDecimal((float) tagCountMap.get(tagName) / totalNum).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
                //各应用
                List<NonWorkingApp> apps = new ArrayList<>();
                String sql2 = "SELECT COUNT(*) AS size  FROM newdataflow \n" +
                        "WHERE \n" +
                        "startTime > #{startTime} \n" +
//                        "AND startTime < #{endTime} \n" +
                        "AND app_tag_collection.keyword = #{tagName} \n" +
                        "GROUP BY \n" +
                        "appName.keyword\n" +
                        "LIMIT 0," + 3 + "\n" +
                        "ORDER BY size DESC";
                String excuteSql2 = SQLBuilder.sql(sql2)
                        .setParamter("startTime", 1523035180000L)
//                        .setParamter("endTime",Long.parseLong(endTime))
                        .setParamter("tagName", tagName)
                        .toString();
                ResultSet appResult = (ResultSet) proxy.queryBySQL(excuteSql2);
                logger.debug(excuteSql2);
                while (appResult.next()) {
                    //具体应用
                    NonWorkingApp app = new NonWorkingApp();
                    app.setAppName(appResult.getString("appName.keyword"));
                    app.setNumOfPerson((int) (appResult.getDouble("size")));
                    apps.add(app);
                }
                tagRatio.setNonWorkingApps(apps);
                result.add(tagRatio);
                Collections.sort(result, new Comparator<TagRatio>() {
                    @Override
                    public int compare(TagRatio o1, TagRatio o2) {
                        if (Integer.parseInt(o1.getTagNum()) < Integer.parseInt(o2.getTagNum())) {
                            return 1;
                        } else if (o1.getTagNum() == o2.getTagNum()) {
                            return 0;
                        } else {
                            return -1;
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    @Override
    public Object listTopOfAppRatio(String startTime, String endTime, int tagNum, int appNum, UserContext context, double threshold) {
        //获取ES连接
        EsProxy esProxy = ProxyFactory.createProxy(DAOProxyEnum.ES);
        TransportClient esClient = esProxy.getClient();

        long startTimeStamp = 0;
        try {
            startTimeStamp = DateUtil.dateToStartOrEndStamp(startTime, 0);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long endTimeStamp = 0;
        try {
            endTimeStamp = DateUtil.dateToStartOrEndStamp(endTime, 1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /**
         * 1.查询ES
         */
        SearchResponse response = searchFromEs(esProxy, esClient, startTimeStamp, endTimeStamp);
        /**
         * 2 根据阈值得到无关应用TOP n 的标签名，访问IPs以及各IP的访问次数，
         */
        List<Object[]> tagIpCount = countByTag(response);
        /**
         * 3 针对Ip进行Reduce操作,统计满足阈值的各标签的实际访问人数，并进行标签排名
         */
        Map<String, Long> tagNumberMap = reduceIp(tagIpCount, threshold, tagNum);
        /**
         * 4 根据标签名，查询ES，找出使用最多的应用排名
         */
        HashMap<String, List<String>> countByAppMap = countByTagApp(esProxy, esClient, new ArrayList(tagNumberMap.keySet()), appNum, startTimeStamp, endTimeStamp);
        /**
         * 5 封装数据并返回
         */
        List<TagRatio> result = returnResult(tagNumberMap, countByAppMap);
        //返回ES连接
        esProxy.returnClient();

        return result;
    }

    private SearchResponse searchFromEs(EsProxy esProxy, TransportClient esClient, long startTimeStamp, long endTimeStamp) {

        List<String> tags = baseDAO.listTagsOfNonWorking();
        SearchRequestBuilder builder = esClient.prepareSearch(ES_INDEX_NAME)
                .setTypes("tweet")
                .setQuery(QueryBuilders.boolQuery().should(QueryBuilders.matchPhraseQuery("app_tag_collection.keyword", tags))
                        .must(QueryBuilders.matchPhraseQuery("isIntranet", 1))
                        .must(QueryBuilders.rangeQuery("startTime").from(startTimeStamp, true).to(endTimeStamp, true)))
                .addAggregation(AggregationBuilders.terms("tagName").field("app_tag_collection.keyword").size(10000)
                        .subAggregation(AggregationBuilders.terms("sourceIP").field("sourceIP").size(10000)
                                .subAggregation(AggregationBuilders.count("count").field("_index"))
                        )
                );
        SearchResponse sr = esProxy.action(builder);
        return sr;
    }

    private List<Object[]> countByTag(SearchResponse response) {
        List<Object[]> resultSet = new ArrayList<>();
        Terms tagNameTerm = response.getAggregations().get("tagName");
        for (Terms.Bucket bucket : tagNameTerm.getBuckets()) {
            String currentTagName = bucket.getKeyAsString();
            Terms sourceIPs = bucket.getAggregations().get("sourceIP");
            for (Terms.Bucket ipBucket : sourceIPs.getBuckets()) {
                String sourceIp = ipBucket.getKeyAsString();
                ValueCount valueCount = ipBucket.getAggregations().get("count");
                long count = valueCount.getValue();
                resultSet.add(new Object[]{currentTagName, sourceIp, count});
            }
        }
        return resultSet;
    }

    private Map<String, Long> reduceIp(List<Object[]> resultSet, double threshold, int tagNum) {
        Map<String, Long> tagCountMap = new HashMap<>();
        //{标签：{工号：点击次数}}
        Map<String, Map<String, Long>> tagNumberCountMap = new HashMap<>();
        Map<String, String> ipNumberMap = baseDAO.getAllEmployeeIpAndNumber();
        for (Object[] obj : resultSet) {
            String tagName = (String) obj[0];
            String sourceIp = (String) obj[1];
            long count = (long) obj[2];
            String employeeNumber = ipNumberMap.get(sourceIp);
            if (employeeNumber == null) {
                continue;
            }
            if (tagNumberCountMap.keySet().contains(tagName)) {
                if (tagNumberCountMap.get(tagName).containsKey(employeeNumber)) {
                    long newCount = tagNumberCountMap.get(tagName).get(employeeNumber) + count;
                    tagNumberCountMap.get(tagName).put(employeeNumber, newCount);
                } else {
                    tagNumberCountMap.get(tagName).put(employeeNumber, count);
                }
            } else {
                Map<String, Long> numberCountMap = new HashMap<>(ipNumberMap.size());
                numberCountMap.put(employeeNumber, count);
                tagNumberCountMap.put(tagName, numberCountMap);
            }
        }
        //大于阈值才计入统计
        for (String tagName : tagNumberCountMap.keySet()) {
            long num = 0;
            for (String number : tagNumberCountMap.get(tagName).keySet()) {
                Long count = tagNumberCountMap.get(tagName).get(number);
                if (count > threshold) {
                    num++;
                    tagCountMap.put(tagName, num);
                }
            }
        }
        List<Map.Entry<String, Long>> list = new ArrayList<Map.Entry<String, Long>>(tagCountMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Long>>() {
            //降序排序
            @Override
            public int compare(Map.Entry<String, Long> o1,
                               Map.Entry<String, Long> o2) {
                if (o2.getValue() > o1.getValue()) {
                    return 1;
                } else if (o2.getValue() < o1.getValue()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        //返回标签排名TOP n
        LinkedHashMap<String, Long> tagCountTopMap = new LinkedHashMap<>();

        for (Map.Entry<String, Long> tag : list) {
            if (tagCountTopMap.size() < tagNum) {
                tagCountTopMap.put(tag.toString().split("=")[0], Long.parseLong(tag.toString().split("=")[1]));
            } else {
                continue;
            }
        }
        return tagCountTopMap;
    }

    private HashMap countByTagApp(EsProxy esProxy, TransportClient esClient, ArrayList tags, int appNum, long startTimeStamp, long endTimeStamp) {
        HashMap map = new HashMap();
        for (Object tagName : tags) {
            ArrayList appList = new ArrayList();
            SearchRequestBuilder builder = esClient.prepareSearch(ES_INDEX_NAME)
                    .setTypes("tweet")
                    .setQuery(QueryBuilders.boolQuery().must(QueryBuilders.matchPhraseQuery("app_tag_collection.keyword", tagName))
                            .must(QueryBuilders.matchPhraseQuery("isIntranet", 1))
                            .must(QueryBuilders.rangeQuery("startTime").from(startTimeStamp, true).to(endTimeStamp, true)))
                    .addAggregation(AggregationBuilders.terms("appName").field("appName.keyword").size(appNum)
                            .subAggregation(AggregationBuilders.count("count").field("_index")
                            )
                    );
            SearchResponse response = esProxy.action(builder);
            Terms appNameTerm = response.getAggregations().get("appName");
            for (Terms.Bucket appNameBucket : appNameTerm.getBuckets()) {
                String appName = appNameBucket.getKeyAsString();
                appList.add(appName);
            }
            map.put(tagName, appList);
        }
        return map;
    }

    private List<TagRatio> returnResult(Map<String, Long> countByTagMap, Map<String, List<String>> countByAppMap) {
        List<TagRatio> result = new ArrayList<>();
        int totalNum = baseDAO.totalNumberOfEmployeesOnGuard();
        for (String tagName : countByTagMap.keySet()) {
            TagRatio tagRatio = new TagRatio();
            tagRatio.setTagNum(String.valueOf(countByTagMap.get(tagName)));
            tagRatio.setTotalNum(String.valueOf(totalNum));
            tagRatio.setTagName(tagName);
//            tagRatio.setRatio(NumberUtil.decimalToPercentage(new BigDecimal((float) countByTagMap.get(tagName) / totalNum).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
            List<NonWorkingApp> appList = new ArrayList<>();
            for (String appName : countByAppMap.get(tagName)) {
                NonWorkingApp app = new NonWorkingApp();
                app.setAppName(appName);
                appList.add(app);
            }
            tagRatio.setNonWorkingApps(appList);
            result.add(tagRatio);
        }
        return result;
    }


}
