package com.handge.bigdata.resource.service.impl.monitor;

import com.handge.bigdata.UnifiedException;
import com.handge.bigdata.dao.ProxyFactory;
import com.handge.bigdata.dao.api.IBaseDAO;
import com.handge.bigdata.dao.proxy.EsProxy;
import com.handge.bigdata.dao.proxy.Proxy;
import com.handge.bigdata.dao.proxy.RedisProxy;
import com.handge.bigdata.enumeration.*;
import com.handge.bigdata.resource.models.request.monitor.NetStatusParam;
import com.handge.bigdata.resource.models.response.monitor.*;
import com.handge.bigdata.resource.models.response.statistics.NonWorkingSoftNew;
import com.handge.bigdata.resource.service.api.monitor.INetStatus;
import com.handge.bigdata.utils.CollectionUtils;
import com.handge.bigdata.utils.DateUtil;
import com.handge.bigdata.utils.LogUtil;
import com.handge.bigdata.utils.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.max.InternalMax;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.elasticsearch.search.sort.SortOrder;
import org.hibernate.dialect.Sybase11Dialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/***
 *
 * @author XiangChao
 * @date 2018/5/16 13:10
 **/
@Component
public class NetStatusImpl implements INetStatus {
    /**
     * 基础信息接口
     */
    @Autowired
    private IBaseDAO baseDAO;

    @Deprecated
    public Object listNetStatusOld(NetStatusParam netStatusParam) {
        Jedis edis = new Jedis("172.18.199.37");
        Set<String> keys = edis.keys("*");
        HashMap<String, String> hashMap = new HashMap<>(16);
        for (String key : keys) {
            NetStatus netStatus = new NetStatus();
            if (key.contains("|") && !key.contains("over")) {
                hashMap.put(key, new BigDecimal(edis.hgetAll(key).size() / 150.00 / 0.01).setScale(1, 1) + "");
                netStatus.setTime(key);
            } else if (key.contains("over")) {
                Set<String> smembers = edis.smembers(key);
                for (String str : smembers) {
                    double v = Integer.parseInt(str) / 150.00 / 0.01;
                    hashMap.put(key, new BigDecimal(v).setScale(1, 1) + "");
                }
            }
        }
        hashMap.remove("14|10|0");
        hashMap.remove("14|10|1");
        hashMap.remove("14|10|2");
        HashMap<String, HashMap<String, String>> hashMapMapping = hashGroup(hashMap);
        Set<String> stringsKey = hashMapMapping.keySet();
        NetStatusList netStatusList = new NetStatusList();
        ArrayList<NetStatus> arrayList = new ArrayList<>();
        for (String timeKey : stringsKey) {
            HashMap<String, String> hashMapR = hashMapMapping.get(timeKey);
            NetStatus netStatus = new NetStatus();
            netStatus.setTime(timeKey.replace("|", ":"));
            netStatus.setNumOfAll("200");
            ArrayList<SubNetStatus> arrayListSub = new ArrayList<>();
            SubNetStatus subNetStatus1 = new SubNetStatus();
            subNetStatus1.setType("工作相关");
            subNetStatus1.setNumberOfPerson(getPersonNumber(hashMapR.get(timeKey + "|1")));
//            subNetStatus1.setNumberOfRatio(hashMapR.get(timeKey + "|1"));
            SubNetStatus subNetStatus0 = new SubNetStatus();
            subNetStatus0.setType("工作不相关");
            subNetStatus0.setNumberOfPerson(getPersonNumber(hashMapR.get(timeKey + "|0")));
//            subNetStatus0.setNumberOfRatio(hashMapR.get(timeKey + "|0"));
            SubNetStatus subNetStatus2 = new SubNetStatus();
            subNetStatus2.setType("不确定");
            subNetStatus2.setNumberOfPerson(getPersonNumber(hashMapR.get(timeKey + "|2")));
//            subNetStatus2.setNumberOfRatio(hashMapR.get(timeKey + "|2"));
            SubNetStatus subNetStatusOverplus = new SubNetStatus();
            subNetStatusOverplus.setType("未上网");
            subNetStatusOverplus.setNumberOfPerson(getPersonNumber(hashMapR.get(timeKey + "|overplus")));
//            subNetStatusOverplus.setNumberOfRatio(hashMapR.get(timeKey + "|overplus"));
            arrayListSub.add(subNetStatus1);
            arrayListSub.add(subNetStatus0);
            arrayListSub.add(subNetStatus2);
            arrayListSub.add(subNetStatusOverplus);
            netStatus.setSubNetStatuses(arrayListSub);
            arrayList.add(netStatus);
        }
        netStatusList.setNetStatuses(arrayList);
        return netStatusList;
    }

    @Override
    public Object listNetStatus(NetStatusParam netStatusParam) {
        String departmentName = netStatusParam.getDepartment();
        //定义生效时间
        Long startTimeMills = DateUtil.getTimeByMinute(-Double.valueOf(baseDAO.getConfigParam().get("QUERY_TIME").toString()));
        Long nowTimeMills = System.currentTimeMillis();
        EsProxy esProxy = ProxyFactory.createProxy(DAOProxyEnum.ES);
        TransportClient esClient = esProxy.getClient();
        String today = DateUtil.timeStampToStrDate(System.currentTimeMillis(), DateFormatEnum.DAY);
        String[] indices = esProxy.generateIndices(today, today, ESIndexEnum.MAPPING);
        //获取员工在岗人数
        double sumPeople = (double) baseDAO.totalNumberOfEmployeesOnGuard();
        BoolQueryBuilder builder = QueryBuilders.boolQuery().filter(QueryBuilders.rangeQuery("startTime").gte(startTimeMills).lte(nowTimeMills));
        if (StringUtils.notEmpty(netStatusParam.getDepartment())) {
            if (baseDAO.getEmployeeIps(departmentName).size() == 0) {
                throw new UnifiedException("部门名称不存在 ", ExceptionWrapperEnum.IllegalArgumentException);
            }
            builder.filter(QueryBuilders.termsQuery("localIp", baseDAO.getEmployeeIps(departmentName).get(departmentName)));
            HashMap<String, Integer> stringIntegerHashMap = baseDAO.numberOfEmployeesGroupByDep();
            if (!stringIntegerHashMap.keySet().contains(netStatusParam.getDepartment())) {
                return new NewNetStatus();
            }
            sumPeople = (double) stringIntegerHashMap.get(netStatusParam.getDepartment());
        }
        SearchRequestBuilder searchRequestBuilder = esClient.prepareSearch(indices)
                .setTypes("data").setQuery(builder)
                .addAggregation(AggregationBuilders.terms("sourceIP").field("localIp")
                        .subAggregation(AggregationBuilders.max("startTime").field("startTime")).size(10000)).setSize(10000);

        SearchResponse sr = esProxy.action(searchRequestBuilder);
        Terms sourceIP = sr.getAggregations().get("sourceIP");
        //获取员工IP与编号
        HashMap<String, String> allEmployeeIpAndNumber = baseDAO.getAllEmployeeIpAndNumber();
        HashMap<String, String> hashMapNumberAndIp = CollectionUtils.hashMapInterchange(allEmployeeIpAndNumber);
        //获取基础标签与对应的抽象标签
        HashMap<String, String> jobClass = baseDAO.getJobClass();
        HashMap<String, ArrayList<String>> employeeIps = baseDAO.getEmployeeIps(netStatusParam.getDepartment());
        //定义工作类
        HashMap<String, String> hashMapWork = new HashMap<>(16);
        //定义非工作类
        HashMap<String, String> hashMapNoWork = new HashMap<>(16);
        //定义不确定
        HashMap<String, String> hashMapUndefine = new HashMap<>(16);
        for (Terms.Bucket bucket : sourceIP.getBuckets()) {
            String ip = bucket.getKeyAsString();
            double time = ((InternalMax) bucket.getAggregations().get("startTime")).getValue();

            if (allEmployeeIpAndNumber.containsKey(ip)) {
                SearchRequestBuilder requestBuilderSub = esClient.prepareSearch(indices).setTypes("data")
                        .setQuery(QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("localIp", ip))
                                .must(QueryBuilders.termsQuery("startTime", new BigDecimal(time).toString())))
                        .addAggregation(AggregationBuilders.terms("app_tag_collection").field("appTags")
                                .subAggregation(AggregationBuilders.count("count").field("_index"))
                                .size(1)).addSort("_index", SortOrder.DESC).setSize(100);

                SearchResponse sp = (SearchResponse) esProxy.action(requestBuilderSub);
                Terms app = sp.getAggregations().get("app_tag_collection");
                for (Terms.Bucket bucketSub : app.getBuckets()) {
                    String appClass = bucketSub.getKeyAsString();
                    ValueCount count = bucketSub.getAggregations().get("count");
                    String appClassStr = jobClass.get(appClass);
                    if (appClassStr == null) {
                        hashMapUndefine.put(allEmployeeIpAndNumber.get(ip), ip);
                    } else if (JobPropertyEnum.工作相关.getCode().equals(appClassStr)) {
                        hashMapWork.put(allEmployeeIpAndNumber.get(ip), ip);
                    } else if (JobPropertyEnum.工作无关.getCode().equals(appClassStr)) {
                        hashMapNoWork.put(allEmployeeIpAndNumber.get(ip), ip);
                    } else {
                        hashMapUndefine.put(allEmployeeIpAndNumber.get(ip), ip);
                    }
                }
            }
        }
        //ElasticSearch连接回收
        esProxy.returnClient();
        NewNetStatus newNetStatus = new NewNetStatus();
        newNetStatus.setNumOfAll(new BigDecimal(sumPeople).setScale(0, 0).toString());
        ArrayList<SubNetStatus> arrayListSub = new ArrayList<>();
        //返回值包装
        SubNetStatus subNetStatus1 = new SubNetStatus();
        subNetStatus1.setType("工作相关");
        subNetStatus1.setNumberOfPerson(String.valueOf(hashMapWork.size()));
//        subNetStatus1.setNumberOfRatio(getPercent(String.valueOf(hashMapWork.size() / sumPeople)));
        SubNetStatus subNetStatus0 = new SubNetStatus();
        subNetStatus0.setType("工作不相关");
        subNetStatus0.setNumberOfPerson(String.valueOf(hashMapNoWork.size()));
//        subNetStatus0.setNumberOfRatio(getPercent(String.valueOf(hashMapNoWork.size() / sumPeople)));
        SubNetStatus subNetStatus2 = new SubNetStatus();
        subNetStatus2.setType("不确定");
        subNetStatus2.setNumberOfPerson(String.valueOf(hashMapUndefine.size()));
//        subNetStatus2.setNumberOfRatio(getPercent(String.valueOf(hashMapUndefine.size() / sumPeople)));
        SubNetStatus subNetStatusOverplus = new SubNetStatus();
        double noSurf = sumPeople - hashMapUndefine.size() - hashMapWork.size() - hashMapNoWork.size();
        subNetStatusOverplus.setType("未上网");
        subNetStatusOverplus.setNumberOfPerson(new BigDecimal(noSurf).setScale(0, 0).toString());
//        subNetStatusOverplus.setNumberOfRatio(getNoSurf(getPercent(String.valueOf(hashMapWork.size() / sumPeople)), getPercent(String.valueOf(hashMapNoWork.size() / sumPeople)), getPercent(String.valueOf(hashMapUndefine.size() / sumPeople))));
        arrayListSub.add(subNetStatus1);
        arrayListSub.add(subNetStatus0);
        arrayListSub.add(subNetStatus2);
        arrayListSub.add(subNetStatusOverplus);
        newNetStatus.setSubNetStatuList(arrayListSub);
//        RedisProxy proxy = ProxyFactory.createProxy(DAOProxyEnum.Redis);
//        Jedis redis = proxy.getConnection();
//        String time = DateUtil.date2Str(new Date(), DateFormatEnum.MINUTES);
//        if (!redis.exists(time)) {
//            redis.hset(time, "0", subNetStatus0.getNumberOfPerson());
//            redis.hset(time, "1", subNetStatus1.getNumberOfPerson());
//            redis.hset(time, "2", subNetStatus2.getNumberOfPerson());
//            redis.hset(time, "3", subNetStatusOverplus.getNumberOfPerson());
//            try {
//                redis.expireAt(time, Long.valueOf(String.valueOf(DateUtil.dateToStartOrEndStamp(time + ":00", 1)).substring(0, 10)));
//            } catch (ParseException e) {
//                throw new UnifiedException(e);
//            }
//        }
//        ArrayList<NetStatus> historyData = getHistoryData(redis, sumPeople);
//        //返回redis连接
//        proxy.returnRedis(redis);
        return newNetStatus;
    }

    @Override
    public Object listNetStatusNew(NetStatusParam netStatusParam) {
        String departmentName = netStatusParam.getDepartment();
        //定义生效时间
        Long startTimeMills = null;
        try {
            startTimeMills = DateUtil.dateToStartOrEndStamp(DateUtil.date2Str(new Date(), DateFormatEnum.SECONDS),0);
        } catch (ParseException e) {
            throw new UnifiedException(e);
        }
        Long nowTimeMills = System.currentTimeMillis();
        EsProxy esProxy = ProxyFactory.createProxy(DAOProxyEnum.ES);
        TransportClient esClient = esProxy.getClient();
        String today = DateUtil.timeStampToStrDate(System.currentTimeMillis(), DateFormatEnum.DAY);
        String[] indices = esProxy.generateIndices(today, today, ESIndexEnum.MAPPING);
        //获取员工在岗人数
        double sumPeople = (double) baseDAO.totalNumberOfEmployeesOnGuard();
        BoolQueryBuilder builder = QueryBuilders.boolQuery().filter(QueryBuilders.rangeQuery("startTime").gte(startTimeMills).lte(nowTimeMills));
        if (StringUtils.notEmpty(netStatusParam.getDepartment())) {
            if (baseDAO.getEmployeeIps(departmentName).size() == 0) {
                throw new UnifiedException("部门名称不存在 ", ExceptionWrapperEnum.IllegalArgumentException);
            }
            builder.filter(QueryBuilders.termsQuery("localIp", baseDAO.getEmployeeIps(departmentName).get(departmentName)));
            HashMap<String, Integer> stringIntegerHashMap = baseDAO.numberOfEmployeesGroupByDep();
            if (!stringIntegerHashMap.keySet().contains(netStatusParam.getDepartment())) {
                return new NewNetStatus();
            }
            sumPeople = (double) stringIntegerHashMap.get(netStatusParam.getDepartment());
        }else {
            builder.filter(QueryBuilders.termsQuery("localIp", baseDAO.getAllEmployeeIpAndNumber().keySet()));
        }
        SearchRequestBuilder searchRequestBuilder = esClient.prepareSearch(indices)
                .setTypes("data").setQuery(builder)
                .addAggregation(AggregationBuilders.terms("sourceIP").field("localIp").size(1000)
                            .subAggregation(AggregationBuilders.dateHistogram("minuteTen").field("startTime").dateHistogramInterval(DateHistogramInterval.minutes(10))
                                .subAggregation(AggregationBuilders.terms("appTags").field("appTags").size(1))
                                    .subAggregation(AggregationBuilders.max("startTime").field("startTime")))
                );

        System.out.println(searchRequestBuilder);
        SearchResponse sr = esProxy.action(searchRequestBuilder);
        ArrayList<NetStatusHistory> netStatusHistories = wrapEsResult(sr);
        if(netStatusHistories.size()==0){
            return new ArrayList<NetStatus>(){{
                this.add(new NetStatus());
            }};
        }
        Map<String, List<NetStatusHistory>> appAndTag = CollectionUtils.group(netStatusHistories, (r) -> ((NetStatusHistory) r).getTime());
        ArrayList<NetStatus> netStatuses = wrapData(appAndTag,sumPeople);
        netStatuses.sort((r1,r2) -> -r1.getTime().compareTo(r2.getTime()));
        esProxy.returnClient();
        return netStatuses;
    }
    private ArrayList<NetStatus> wrapData(Map<String, List<NetStatusHistory>> map,double sumPeople){
        ArrayList<NetStatus> arrayList = new ArrayList<>();
        Set<String> times = map.keySet();
        for(String time : times){
            NetStatus netStatus = new NetStatus();
            netStatus.setTime(time);
            netStatus.setNumOfAll(String.valueOf((int)sumPeople));
            ArrayList<SubNetStatus> subNetStatuses = new ArrayList<>();
            List<NetStatusHistory> netStatusHistories = map.get(time);
            HashMap<String,Integer> hashMap = new HashMap<>();
            for(NetStatusHistory netStatusHistory : netStatusHistories){
                String appClass = netStatusHistory.getAppClass();
                if(hashMap.get(appClass)==null){
                    hashMap.put(appClass,1);
                }else {
                    hashMap.put(appClass,hashMap.get(appClass)+1);
                }
            }
            Set<String> types = hashMap.keySet();
            int size = types.size();
            int n = 0;
            double count = 0;
            for(String type : types){
                SubNetStatus subNetStatus = new SubNetStatus();
                subNetStatus.setType(type);
                subNetStatus.setNumberOfPerson(String.valueOf(hashMap.get(type)));
                n++;
                count+=hashMap.get(type);
                if(n==size){
                    SubNetStatus subNetStatusNoSuf = new SubNetStatus();
                    subNetStatusNoSuf.setType("未上网");
                    subNetStatusNoSuf.setNumberOfPerson(String.valueOf((int)(sumPeople-count)));
                    subNetStatuses.add(subNetStatusNoSuf);
                }
                subNetStatuses.add(subNetStatus);
            }
            netStatus.setSubNetStatuses(subNetStatuses);
            arrayList.add(netStatus);
        }
        return arrayList;
    }

    private ArrayList<NetStatusHistory> wrapEsResult(SearchResponse sr) {
        //获取基础标签与对应的抽象标签
        HashMap<String, String> jobClass = baseDAO.getJobClass();
        HashMap<String, String> allEmployeeIpAndNumber = baseDAO.getAllEmployeeIpAndNumber();
        ArrayList<NetStatusHistory> netStatusHistories = new ArrayList<>();
        Terms terms = sr.getAggregations().get("sourceIP");
        for (Terms.Bucket ips : terms.getBuckets()){
            Histogram histograms  = ips.getAggregations().get("minuteTen");
            for(Histogram.Bucket minutes:histograms.getBuckets()){
                Terms tags = minutes.getAggregations().get("appTags");
                for(Terms.Bucket bucketTags:tags.getBuckets()){
                    String appClassStr = jobClass.get(bucketTags.getKey().toString());
                    NetStatusHistory netStatusHistory = new NetStatusHistory();
                    netStatusHistory.setIp(allEmployeeIpAndNumber.get(ips.getKey().toString()));
                    netStatusHistory.setTime(DateUtil.transformTimeZone(minutes.getKey().toString()).substring(11, 16));
                    if (appClassStr == null) {
                        netStatusHistory.setAppClass(JobPropertyEnum.不确定.getDesc());
                    } else if (JobPropertyEnum.工作相关.getCode().equals(appClassStr)) {
                        netStatusHistory.setAppClass(JobPropertyEnum.工作相关.getDesc());
                    } else if (JobPropertyEnum.工作无关.getCode().equals(appClassStr)) {
                        netStatusHistory.setAppClass(JobPropertyEnum.工作无关.getDesc());
                    } else {
                        netStatusHistory.setAppClass(JobPropertyEnum.不确定.getDesc());
                    }
                    netStatusHistories.add(netStatusHistory);
                }
            }
        }
        LogUtil.printSomething(netStatusHistories);
        return netStatusHistories;
    }


    private HashMap<String, HashMap<String, String>> hashGroup(HashMap<String, String> hashMap) {
        HashMap<String, HashMap<String, String>> hashMapHashMap = new HashMap<>(16);
        Set<String> strings = hashMap.keySet();
        for (String key : strings) {
            String keySpl = key.split("\\|")[0] + "|" + key.split("\\|")[1];
            if (hashMapHashMap.get(keySpl) == null) {
                hashMapHashMap.put(keySpl, new HashMap<String, String>(16) {{
                    this.put(key, hashMap.get(key));
                }});
            } else {
                hashMapHashMap.get(keySpl).put(key, hashMap.get(key));
            }
        }
        return hashMapHashMap;
    }

    private String getPersonNumber(String rate) {
        return new BigDecimal(Double.valueOf(rate.split("%")[0]) * 200 / 100).setScale(0, 1).toString();
    }

    private String getPercent(String num) {
        return new BigDecimal(num).multiply(new BigDecimal("100")).setScale(1, 1).toString();
    }

    private String getNoSurf(String work, String noWork, String unDefine) {
        return String.valueOf(new BigDecimal(100).subtract(new BigDecimal(work).add(new BigDecimal(noWork)).add(new BigDecimal(unDefine))));
    }

    /**
     * 获取redis数据
     * @param redis
     * @return
     */
    private ArrayList<NetStatus> getHistoryData(Jedis redis,double sumPeople) {
        ArrayList<NetStatus> netStatuses = new ArrayList<>();
        String timeMinutes = DateUtil.date2Str(new Date(), DateFormatEnum.DAY);
        Set<String> keys = redis.keys(timeMinutes + " *");
        for (String key : keys) {
            NetStatus netStatus = new NetStatus();
            Map<String, String> map = redis.hgetAll(key);
            Set<String> clazzs = map.keySet();
            ArrayList<SubNetStatus> SubNetStatusList = new ArrayList<>();
            for (String clazz : clazzs) {
                SubNetStatus subNetStatus = new SubNetStatus();
                String value = redis.hget(key, clazz);
                System.out.println(key + " " + clazz + " " + value);
                if(clazz.equals(JobPropertyEnum.工作无关.getCode())){
                    subNetStatus.setType(JobPropertyEnum.工作无关.getDesc());
                }else if(clazz.equals(JobPropertyEnum.工作相关.getCode())){
                    subNetStatus.setType(JobPropertyEnum.工作相关.getDesc());
                }else if(clazz.equals(JobPropertyEnum.不确定.getCode())){
                    subNetStatus.setType(JobPropertyEnum.不确定.getDesc());
                }else {
                    subNetStatus.setType("未上网");
                }
                subNetStatus.setNumberOfPerson(value);
                SubNetStatusList.add(subNetStatus);
            }
            netStatus.setTime(key);
            netStatus.setSubNetStatuses(SubNetStatusList);
            netStatus.setNumOfAll(String.valueOf((int)sumPeople));
            netStatuses.add(netStatus);
        }

        return netStatuses;
    }
}
