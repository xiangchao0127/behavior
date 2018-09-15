package com.handge.bigdata.resource.service.impl.statistics;

import com.handge.bigdata.UnifiedException;
import com.handge.bigdata.dao.ProxyFactory;
import com.handge.bigdata.dao.api.IBaseDAO;
import com.handge.bigdata.dao.proxy.EsProxy;
import com.handge.bigdata.enumeration.*;
import com.handge.bigdata.resource.models.request.statistics.NonWorkingTimeSoltParam;
import com.handge.bigdata.resource.models.response.statistics.*;
import com.handge.bigdata.resource.service.api.statistics.INonWorkingTimeSolt;
import com.handge.bigdata.utils.CollectionUtils;
import com.handge.bigdata.utils.DateUtil;
import com.handge.bigdata.utils.LogUtil;
import com.handge.bigdata.utils.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/***
 *
 * @author XiangChao
 * @date 2018/5/16 13:15
 **/

@Component
public class NonWorkingTimeSoltImpl implements INonWorkingTimeSolt {

    /**
     * 基础数据库查询Bean
     */
    @Autowired
    IBaseDAO baseDao;


    @Override
    public Object listNonWorkingTimeSolt(NonWorkingTimeSoltParam nonWorkingTimeSoltParam) {
        if(TimeSectionEnum.WEEK_SECTION.equals(nonWorkingTimeSoltParam.getSection())||TimeSectionEnum.DAY_SECTION.equals(nonWorkingTimeSoltParam.getSection())){
            return listNonWorkingTimeSoltDay(nonWorkingTimeSoltParam);
        }
        Long[] time = getTime(nonWorkingTimeSoltParam);
        Long timeStart = time[0];
        Long timeEnd = time[1];
        Map<String, Object> configParamMap = baseDao.getConfigParam();
//        List<String> listClassOther = Arrays.asList(configParamMap.get("NON_WORKING_TIME_SOLT_APP_LIST").toString().split(","));
        List<String> listClass = null;
        if (nonWorkingTimeSoltParam.getJobProperty().equals(JobPropertyEnum.工作相关.getCode())) {
            listClass = baseDao.listTagsOfWorking();
        } else {
            listClass = baseDao.listTagsOfNonWorking();
        }


        /**
         * 查询ES
         */
        SearchResponse response = searchFromEs(nonWorkingTimeSoltParam, listClass, configParamMap, timeStart, timeEnd);

        /**
         * 封装es返回结果
         */
        ArrayList<NonWorkingSoftNew> nonWorkingSoftNews = wrapEsResult2(response);
        /**
         * 通过小时分组
         */
        HashMap<String, ArrayList<String>> hashMapH = groupByHourReduce(nonWorkingSoftNews, listClass);
        /**
         * 封装结果集给前端
         */
        UnrelatedTimeAndPie unrelatedTimeAndPie = resultRrap(hashMapH, timeStart, timeEnd, configParamMap,nonWorkingTimeSoltParam.getJobProperty());

        return unrelatedTimeAndPie;
    }

    /**
     * 按周，天统计
     * @param nonWorkingTimeSoltParam
     * @return
     */
    private Object listNonWorkingTimeSoltDay(NonWorkingTimeSoltParam nonWorkingTimeSoltParam) {
        Long[] time = getTime(nonWorkingTimeSoltParam);
        Long timeStart = time[0];
        Long timeEnd = time[1];
        Map<String, Object> configParamMap = baseDao.getConfigParam();
//        List<String> listClassOther = Arrays.asList(configParamMap.get("NON_WORKING_TIME_SOLT_APP_LIST").toString().split(","));
        List<String> listClass = null;
        if (nonWorkingTimeSoltParam.getJobProperty().equals(JobPropertyEnum.工作相关.getCode())) {
            listClass = baseDao.listTagsOfWorking();
        } else {
            listClass = baseDao.listTagsOfNonWorking();
        }

        /**
         * 查询ES
         */
        SearchResponse response = searchFromEsDay(nonWorkingTimeSoltParam, listClass, configParamMap, timeStart, timeEnd);
        /**
         * 封装查询结果
         */
        ArrayList<NonWorkingSoftNew> nonWorkingSoftNews = wrapEsResultDay(response);
        /**
         * 根据天,标签分组并reduce
         */
        ArrayList<NonWorkingSoftNew> nonWorkingSoftNewData = groupAndReduceResult(nonWorkingSoftNews);
        LogUtil.printSomething(nonWorkingSoftNewData);
        /**
         * 转化为周几并求平均
         */
        ArrayList<NonWorkingSoftNew> nonWorkingSoftWeek = transformWeek(nonWorkingSoftNewData,nonWorkingTimeSoltParam.getSection().getMark());
        LogUtil.printSomething(nonWorkingSoftWeek);

        UnrelatedTimeAndPie unrelatedTimeAndPie = toFront(nonWorkingSoftWeek, configParamMap,nonWorkingTimeSoltParam.getJobProperty());
        return unrelatedTimeAndPie;
    }

    private Long[] getTime(NonWorkingTimeSoltParam nonWorkingTimeSoltParam) {
        Long[] times = new Long[2];
        Long timeStart = null;
        try {
            if (StringUtils.isEmpty(nonWorkingTimeSoltParam.getStartTime())) {
                timeStart = DateUtil.getMonthStartTimeStamp();
            } else {
                timeStart = DateUtil.dateToStartOrEndStamp(nonWorkingTimeSoltParam.getStartTime(), 0);
            }
        } catch (ParseException e) {
            throw new UnifiedException(e);
        }
        Long timeEnd = null;
        try {
            if (StringUtils.isEmpty(nonWorkingTimeSoltParam.getEndTime())) {
                timeEnd = System.currentTimeMillis();
            } else {
                timeEnd = DateUtil.dateToStartOrEndStamp(nonWorkingTimeSoltParam.getEndTime(), 1);
            }
        } catch (ParseException e) {
            throw new UnifiedException(e);
        }
        times[0] = timeStart;
        times[1] = timeEnd;
        return times;
    }

    private UnrelatedTimeAndPie toFront(ArrayList<NonWorkingSoftNew> nonWorkingSoftWeek, Map<String, Object> configParamMap,String jobProperty) {
        UnrelatedTimeAndPie unrelatedTimeAndPie = new UnrelatedTimeAndPie();
        //条形图
        ArrayList<UnrelatedTimes> unrelatedTimes = new ArrayList<>();
        //饼状图
        ArrayList<PieChartData> pieChartDatas = new ArrayList<>();
        //需要的标签
        List<String> listClass = null;
        if (jobProperty.equals(JobPropertyEnum.工作相关.getCode())) {
            listClass = Arrays.asList(configParamMap.get("WORKING_TIME_SOLT_APP_LIST").toString().split(","));
        } else {
            listClass = Arrays.asList(configParamMap.get("NON_WORKING_TIME_SOLT_APP_LIST").toString().split(","));
        }
        //点击一次的有效时间(分钟)
        String minuteByOneClick = (String) configParamMap.get("MINUTE_BY_ONE_CLICK");
        Double minuteByOne = Double.valueOf(minuteByOneClick);
        Map<String, List<NonWorkingSoftNew>> dataGroupByDay = nonWorkingSoftWeek.stream().collect(Collectors.groupingBy(NonWorkingSoftNew::getDay));
        Set<String> days = dataGroupByDay.keySet();
        //总计
        Long sum = 0L;
        //饼状图
        HashMap<String, Long> hashMapClass = new HashMap<>();
        hashMapClass.put("其他", 0L);
        for (String day : days) {
            UnrelatedTimes unrelatedTime = new UnrelatedTimes();
            unrelatedTime.setHour(day);
            ArrayList<UnrelatedTime> subUnrelatedTime = new ArrayList<>();
            List<NonWorkingSoftNew> nonWorkingSoftNews = dataGroupByDay.get(day);
            long count = 0;
            long countSum = 0;
            int size = nonWorkingSoftNews.size();
            int i = 0;
            for (NonWorkingSoftNew nonWorkingSoftNew : nonWorkingSoftNews) {
                if (listClass.contains(nonWorkingSoftNew.getAppClass())) {
                    UnrelatedTime unrelatedTimeSub = new UnrelatedTime();
                    unrelatedTimeSub.setAppClass(nonWorkingSoftNew.getAppClass());
                    unrelatedTimeSub.setCountTime(new BigDecimal(String.valueOf(nonWorkingSoftNew.getCount() * minuteByOne)).setScale(1, 1).toPlainString());
                    subUnrelatedTime.add(unrelatedTimeSub);
                    if (hashMapClass.get(nonWorkingSoftNew.getAppClass()) == null) {
                        hashMapClass.put(nonWorkingSoftNew.getAppClass(), nonWorkingSoftNew.getCount());
                    } else {
                        hashMapClass.put(nonWorkingSoftNew.getAppClass(), nonWorkingSoftNew.getCount() + hashMapClass.get(nonWorkingSoftNew.getAppClass()));
                    }
                } else {
                    count += nonWorkingSoftNew.getCount();
                }
                i++;
                if (i == size) {
                    UnrelatedTime unrelatedTimeOther = new UnrelatedTime();
                    unrelatedTimeOther.setAppClass("其他");
                    unrelatedTimeOther.setCountTime(new BigDecimal(String.valueOf(count * minuteByOne)).setScale(1, 1).toPlainString());
                    subUnrelatedTime.add(unrelatedTimeOther);
                    hashMapClass.put("其他", hashMapClass.get("其他") + count);
                }
                countSum += nonWorkingSoftNew.getCount();
            }
            sum += countSum;
            unrelatedTime.setUnrelatedTimes(subUnrelatedTime);
            unrelatedTime.setNonWorkingTime(new BigDecimal(String.valueOf(countSum * minuteByOne)).setScale(1, 1).toPlainString());
            unrelatedTimes.add(unrelatedTime);
        }
        LogUtil.printSomething(hashMapClass);
        int pieN = 0;
        int pieSize = hashMapClass.size();
        BigDecimal bigDecimal = new BigDecimal("0");
        for (String clazz : hashMapClass.keySet()) {
            PieChartData pieChartData = new PieChartData();
            pieChartData.setAppTag(clazz);//标签
            BigDecimal bigDecimalCount = new BigDecimal(hashMapClass.get(clazz) * 100).divide(new BigDecimal(sum), 1, BigDecimal.ROUND_HALF_UP).setScale(1, 1);
            pieChartData.setRatio(new BigDecimal(hashMapClass.get(clazz) * 100).divide(new BigDecimal(sum), 1, BigDecimal.ROUND_HALF_UP).setScale(1, 1).toString());//占比
            if (pieN == pieSize - 1) {
                pieChartData.setRatio(new BigDecimal("100").subtract(bigDecimal).setScale(1, 1).toString());
            }
            bigDecimal = bigDecimal.add(bigDecimalCount);
            pieChartData.setDuration(new BigDecimal(hashMapClass.get(clazz)).multiply(new BigDecimal(minuteByOne)).setScale(1, 1).toPlainString());//时长
            pieChartDatas.add(pieChartData);
            pieN++;
        }
        unrelatedTimes.sort((r1,r2)->-r1.getHour().compareTo(r2.getHour()));
        unrelatedTimeAndPie.setUnrelatedTimeList(unrelatedTimes);
        unrelatedTimeAndPie.setPieChartDataList(pieChartDatas);
        return unrelatedTimeAndPie;
    }

    private ArrayList<NonWorkingSoftNew> transformWeek(ArrayList<NonWorkingSoftNew> nonWorkingSoftNewData,String mark) {
        ArrayList<NonWorkingSoftNew> arrayList = new ArrayList<>();
        List<NonWorkingSoftNew> NonWorkingSoftNewWeek = null;
        if(TimeSectionEnum.WEEK_SECTION.getMark().equals(mark)){
            NonWorkingSoftNewWeek = nonWorkingSoftNewData.stream().map(new Function<NonWorkingSoftNew, NonWorkingSoftNew>() {
                @Override
                public NonWorkingSoftNew apply(NonWorkingSoftNew nonWorkingSoftNew) {
                    nonWorkingSoftNew.setDay(DateUtil.dateToWeek(nonWorkingSoftNew.getDay()));
                    return nonWorkingSoftNew;
                }
            }).collect(Collectors.toList());
        }else {
            NonWorkingSoftNewWeek = nonWorkingSoftNewData;
        }
        Map<String, List<NonWorkingSoftNew>> groupWeek = CollectionUtils.group(NonWorkingSoftNewWeek, r -> ((NonWorkingSoftNew) r).getDay() + "=" + ((NonWorkingSoftNew) r).getAppClass());
        Set<String> weeks = groupWeek.keySet();
        for (String week : weeks) {
            NonWorkingSoftNew nonWorkingSoftNew = new NonWorkingSoftNew();
            List<NonWorkingSoftNew> nonWorkingSoftNews = groupWeek.get(week);
            Long avgCount = CollectionUtils.reduce(nonWorkingSoftNews, 0L, (d, l) -> (Long.valueOf(l.getCount()) + d) / nonWorkingSoftNews.size());
            nonWorkingSoftNew.setDay(week.split("=")[0]);//周几
            nonWorkingSoftNew.setAppClass(week.split("=")[1]);//标签
            nonWorkingSoftNew.setCount(avgCount);//平均次数
            arrayList.add(nonWorkingSoftNew);
        }
        return arrayList;
    }

    private ArrayList<NonWorkingSoftNew> groupAndReduceResult(ArrayList<NonWorkingSoftNew> nonWorkingSoftNews) {
        ArrayList<NonWorkingSoftNew> arrayList = new ArrayList<>();
        Map<String, List<NonWorkingSoftNew>> dayData = CollectionUtils.group(nonWorkingSoftNews, r -> ((NonWorkingSoftNew) r).getDay() + "=" + ((NonWorkingSoftNew) r).getAppClass());
        Set<String> days = dayData.keySet();
        for (String dayAndApp : days) {
            List<NonWorkingSoftNew> nonWorkingSoftNewsDay = dayData.get(dayAndApp);
            Long counts = CollectionUtils.reduce(nonWorkingSoftNewsDay, 0L, (d, p) -> Long.valueOf(p.getCount()) + d);
            NonWorkingSoftNew nonWorkingSoftNew = new NonWorkingSoftNew();
            nonWorkingSoftNew.setDay(dayAndApp.split("=")[0]);
            if (dayAndApp.split("=").length > 1) {
                nonWorkingSoftNew.setAppClass(dayAndApp.split("=")[1]);
                nonWorkingSoftNew.setCount(counts);
                arrayList.add(nonWorkingSoftNew);
            }
        }
        return arrayList;
    }

//    /**
//     * 填充空白标签数据
//     * @param unrelatedTimes
//     * @param listClass
//     */
//    private void fillBankApp(ArrayList<UnrelatedTimes> unrelatedTimes, List<String> listClass) {
//        ArrayList<UnrelatedTimes> unrelatedTimesNew = new ArrayList<>();
//        for(UnrelatedTimes unrelatedTime : unrelatedTimes){
//            ArrayList<UnrelatedTime> unrelatedTimess = unrelatedTime.getUnrelatedTimes();
//            for(UnrelatedTime unrelatedT : unrelatedTimess){
//                if(listClass.contains(unrelatedT.getAppClass())){
//                    continue;
//                }else {
//
//                }
//            }
//        }
//    }

    private SearchResponse searchFromEs(NonWorkingTimeSoltParam nonWorkingTimeSoltParam, List<String> listClass, Map<String, Object> configParamMap, long timeStart, long timeEnd) {

        EsProxy esProxy = ProxyFactory.createProxy(DAOProxyEnum.ES);
        String[] indices = esProxy.generateIndices(timeStart, timeEnd, ESIndexEnum.MAPPING);
        TransportClient client = esProxy.getClient();
        String esType = configParamMap.get("ES_TYPE").toString();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().filter(QueryBuilders.rangeQuery("startTime").gte(timeStart).lte(timeEnd))
                .filter(QueryBuilders.termsQuery("appTags", listClass));
        if (StringUtils.notEmpty(nonWorkingTimeSoltParam.getDepartment())) {
            Map map = baseDao.getEmployeeIps(nonWorkingTimeSoltParam.getDepartment());
            if (map.size() == 0) {
                throw new UnifiedException("部门名称不存在 ", ExceptionWrapperEnum.IllegalArgumentException);
            }
            queryBuilder.filter(QueryBuilders.termsQuery("localIp", (ArrayList) map.get(nonWorkingTimeSoltParam.getDepartment())));
        }
        SearchRequestBuilder builder = client.prepareSearch().setIndices(indices)
                .setTypes(esType)
                .setQuery(queryBuilder)
                .addAggregation(AggregationBuilders.dateHistogram("day").field("startTime")
                        .dateHistogramInterval(DateHistogramInterval.DAY)
                        .subAggregation(AggregationBuilders.dateHistogram("hour").field("startTime")
                                .dateHistogramInterval(DateHistogramInterval.HOUR)
                                .subAggregation(AggregationBuilders.terms("app_tag_collection").field("appTags").size(10000)
                                        .subAggregation(AggregationBuilders.terms("sourceIP").field("localIp").size(10000)))
                        ));
        SearchResponse response = esProxy.action(builder);
        //返回es连接
        esProxy.returnClient();
        return response;
    }

    private SearchResponse searchFromEsDay(NonWorkingTimeSoltParam nonWorkingTimeSoltParam, List<String> listClass, Map<String, Object> configParamMap, long timeStart, long timeEnd) {

        EsProxy esProxy = ProxyFactory.createProxy(DAOProxyEnum.ES);
        String[] indices = esProxy.generateIndices(timeStart, timeEnd, ESIndexEnum.MAPPING);
        TransportClient client = esProxy.getClient();
        String esType = configParamMap.get("ES_TYPE").toString();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery().filter(QueryBuilders.rangeQuery("startTime").gte(timeStart).lte(timeEnd))
                .filter(QueryBuilders.termsQuery("appTags", listClass));
        if (StringUtils.notEmpty(nonWorkingTimeSoltParam.getDepartment())) {
            Map map = baseDao.getEmployeeIps(nonWorkingTimeSoltParam.getDepartment());
            if (map.size() == 0) {
                throw new UnifiedException("部门名称不存在 ", ExceptionWrapperEnum.IllegalArgumentException);
            }
            queryBuilder.filter(QueryBuilders.termsQuery("localIp", (ArrayList) map.get(nonWorkingTimeSoltParam.getDepartment())));
        }
        SearchRequestBuilder builder = client.prepareSearch().setIndices(indices)
                .setTypes(esType)
                .setQuery(queryBuilder)
                .addAggregation(AggregationBuilders.dateHistogram("day").field("startTime")
                        .dateHistogramInterval(DateHistogramInterval.DAY)
                        .subAggregation(AggregationBuilders.terms("app_tag_collection").field("appTags").size(10000)
                                .subAggregation(AggregationBuilders.terms("sourceIP").field("localIp").size(10000)))
                );
        SearchResponse response = esProxy.action(builder);
        //返回es连接
        esProxy.returnClient();
        return response;
    }

    private ArrayList<NonWorkingSoftNew> wrapEsResult2(SearchResponse sr) {
        //获取员工ip与编号
        HashMap<String, String> allEmployeeIpAndNumber = baseDao.getAllEmployeeIpAndNumber();

        ArrayList<NonWorkingSoftNew> nonWorkingSoftNews = new ArrayList<>();
        Histogram histograms = sr.getAggregations().get("day");
        for (Histogram.Bucket histogram : histograms.getBuckets()) {
            String date = histogram.getKeyAsString();
            Histogram dates = histogram.getAggregations().get("hour");
            for (Histogram.Bucket hour : dates.getBuckets()) {
                String hourName = hour.getKey().toString();
                Terms apps = hour.getAggregations().get("app_tag_collection");
                for (Terms.Bucket app : apps.getBuckets()) {
                    String appClass = app.getKey().toString();
                    Terms sourceIPs = app.getAggregations().get("sourceIP");
                    for (Terms.Bucket sourceIP : sourceIPs.getBuckets()) {
                        String iP = sourceIP.getKey().toString();
                        long value = sourceIP.getDocCount();
                        if (allEmployeeIpAndNumber.containsKey(iP)) {
                            NonWorkingSoftNew nonWorkingSoftNew = new NonWorkingSoftNew();
                            nonWorkingSoftNew.setDay(DateUtil.timeStampToStrDate(Long.parseLong(date), DateFormatEnum.DAY));
                            nonWorkingSoftNew.setHour(DateUtil.transformTimeZone(hourName).substring(11, 13));
                            nonWorkingSoftNew.setAppClass(appClass);
                            nonWorkingSoftNew.setIp(iP);
                            nonWorkingSoftNew.setCount(value);
                            nonWorkingSoftNews.add(nonWorkingSoftNew);
                        }
                    }
                }
            }
        }
        return nonWorkingSoftNews;
    }

    private ArrayList<NonWorkingSoftNew> wrapEsResultDay(SearchResponse sr) {
        //获取员工ip与编号
        HashMap<String, String> allEmployeeIpAndNumber = baseDao.getAllEmployeeIpAndNumber();

        ArrayList<NonWorkingSoftNew> nonWorkingSoftNews = new ArrayList<>();
        Histogram histograms = sr.getAggregations().get("day");
        for (Histogram.Bucket day : histograms.getBuckets()) {
            String date = day.getKeyAsString();
            Terms apps = day.getAggregations().get("app_tag_collection");
            for (Terms.Bucket app : apps.getBuckets()) {
                String appClass = app.getKey().toString();
                Terms sourceIPs = app.getAggregations().get("sourceIP");
                for (Terms.Bucket sourceIP : sourceIPs.getBuckets()) {
                    String iP = sourceIP.getKey().toString();
                    long value = sourceIP.getDocCount();
                    if (allEmployeeIpAndNumber.containsKey(iP)) {
                        NonWorkingSoftNew nonWorkingSoftNew = new NonWorkingSoftNew();
                        nonWorkingSoftNew.setDay(DateUtil.timeStampToStrDate(Long.parseLong(date), DateFormatEnum.DAY));
                        nonWorkingSoftNew.setAppClass(appClass);
                        nonWorkingSoftNew.setIp(iP);
                        nonWorkingSoftNew.setCount(value);
                        nonWorkingSoftNews.add(nonWorkingSoftNew);
                    }
                }
            }
        }
        LogUtil.printSomething(nonWorkingSoftNews);
        return nonWorkingSoftNews;
    }


    private HashMap<String, ArrayList<String>> groupByHourReduce(ArrayList<NonWorkingSoftNew> nonWorkingSoftNews, List<String> listClass) {
        HashMap<String, Long> hashMap = new HashMap<>();
        //小时-分类 总次数
        for (NonWorkingSoftNew nonWorkingSoftNew : nonWorkingSoftNews) {
            if (listClass.contains(nonWorkingSoftNew.getAppClass())) {
                String key = nonWorkingSoftNew.getHour() + "-" + nonWorkingSoftNew.getAppClass();
                if (hashMap.get(key) == null) {
                    hashMap.put(nonWorkingSoftNew.getHour() + "-" + nonWorkingSoftNew.getAppClass(), nonWorkingSoftNew.getCount());
                } else {
                    hashMap.put(key, hashMap.get(key) + nonWorkingSoftNew.getCount());
                }
            }
        }
        Set<String> stringKey = hashMap.keySet();
        HashMap<String, ArrayList<String>> hashMapHour = new HashMap<>();
        //小时 分类=次数
        for (String str : stringKey) {
            if (hashMapHour.get(str.split("-")[0]) == null) {
                hashMapHour.put(str.split("-")[0], new ArrayList<>(Arrays.asList(str.split("-")[1] + "=" + hashMap.get(str))));
            } else {
                ArrayList<String> list = hashMapHour.get(str.split("-")[0]);
                list.add(str.split("-")[1] + "=" + hashMap.get(str));
                hashMapHour.put(str.split("-")[0], list);
            }
        }
        return hashMapHour;
    }

    private UnrelatedTimeAndPie resultRrap(HashMap<String, ArrayList<String>> hashMapH, long startTime, long endTime, Map<String, Object> configMap,String jobProperty) {
        Integer sumPeople = baseDao.totalNumberOfEmployeesOnGuard();
        Map<String, Object> configParamMap = baseDao.getConfigParam();
        List<String> listClassOther = null;
        if (jobProperty.equals(JobPropertyEnum.工作相关.getCode())) {
            listClassOther = Arrays.asList(configParamMap.get("WORKING_TIME_SOLT_APP_LIST").toString().split(","));
        } else {
            listClassOther = Arrays.asList(configParamMap.get("NON_WORKING_TIME_SOLT_APP_LIST").toString().split(","));
        }
        //hashMapH 小时 分类=次数
        ArrayList<PieChartData> pieChartDatas = new ArrayList<>();
        ArrayList<UnrelatedTimes> unrelatedTimess = new ArrayList<>();
        //计算相隔天数
        String day = null;
        try {
            day = String.valueOf(DateUtil.differentDays(DateUtil.timeStampToStrDate(startTime, DateFormatEnum.SECONDS), DateUtil.timeStampToStrDate(endTime, DateFormatEnum.SECONDS), DateFormatEnum.SECONDS));
        } catch (ParseException e) {
            throw new UnifiedException(e);
        }
        Set<String> hours = hashMapH.keySet();
        /**
         * 过滤不需要的时段
         */
        String[] timePoint = configMap.get("NON_WORKING_TIME_SOLT_TIME_POINT").toString().split(",");
        String minuteByOneClick = (String) configMap.get("MINUTE_BY_ONE_CLICK");
        hours.removeIf(str -> !Arrays.asList(timePoint).contains(str));
        int sumCount = 0;
        HashMap<String, Double> hashMapPie = new HashMap<>();
        hashMapPie.put("其他", 0.0);
        for (String hour : hours) {
            UnrelatedTimes unrelatedTimes = new UnrelatedTimes();
            unrelatedTimes.setHour(hour);
            ArrayList<String> arrayListHour = hashMapH.get(hour);
            int sum = 0;
            ArrayList<UnrelatedTime> arrayList = new ArrayList<>();
            int size = arrayListHour.size();
            int n = 0;
            HashMap<String, Integer> hashMap = new HashMap<>();
            hashMap.put("其他", 0);
            for (String appCount : arrayListHour) {
                UnrelatedTime unrelatedTime = new UnrelatedTime();
                unrelatedTime.setAppClass(appCount.split("=")[0]);
                unrelatedTime.setCountTime(handlePercent(appCount.split("=")[1], day, minuteByOneClick));
                sum += Integer.parseInt(appCount.split("=")[1]);
                if (listClassOther.contains(appCount.split("=")[0])) {
                    arrayList.add(unrelatedTime);
                    if (hashMapPie.get(appCount.split("=")[0]) == null) {
                        hashMapPie.put(appCount.split("=")[0], Double.valueOf(appCount.split("=")[1]));
                    } else {
                        hashMapPie.put(appCount.split("=")[0], Double.valueOf(appCount.split("=")[1]) + hashMapPie.get(appCount.split("=")[0]));
                    }
                } else {
                    hashMap.put("其他", hashMap.get("其他") + Integer.parseInt(appCount.split("=")[1]));
                    hashMapPie.put("其他", Double.valueOf(appCount.split("=")[1]) + hashMapPie.get("其他"));
                }

//                n++;
//                if(size==n){
//                    /**
//                     * 填充分类空白数据
//                     */
//                    for(UnrelatedTime unrelatedTimeNew:arrayList){
//                        if(listClass.contains(unrelatedTimeNew.getAppClass())){
//                            continue;
//                        }
//                    }
//                }
            }
            UnrelatedTime unrelatedTimeOther = new UnrelatedTime();
            unrelatedTimeOther.setAppClass("其他");
            unrelatedTimeOther.setCountTime(handlePercent(String.valueOf(hashMap.get("其他")), day, minuteByOneClick));
            arrayList.add(unrelatedTimeOther);
            unrelatedTimes.setUnrelatedTimes(arrayList);
            unrelatedTimes.setNonWorkingTime(handlePercent(String.valueOf(sum), day, minuteByOneClick));
            unrelatedTimess.add(unrelatedTimes);
            sumCount += sum;
        }
        //分类 次数
        Set<String> stringsKey = hashMapPie.keySet();
        int size = stringsKey.size();
        int n = 0;
        BigDecimal bigDecimal = new BigDecimal("0");
        BigDecimal bigDecimalTime = new BigDecimal("0");
        /**
         * 计算饼状图百分比
         */
        for (String app : stringsKey) {
            PieChartData pieChartData = new PieChartData();
            if (n == size - 1) {
                pieChartData.setRatio(new BigDecimal("100.00").subtract(bigDecimal).setScale(1, 0).toString());
                pieChartData.setDuration(new BigDecimal(sumCount).multiply(new BigDecimal(configMap.get("MINUTE_BY_ONE_CLICK").toString())).divide(new BigDecimal(day).multiply(new BigDecimal(sumPeople)), 1, RoundingMode.HALF_UP).subtract(bigDecimalTime).setScale(1, 0).toPlainString());
            } else {
                pieChartData.setRatio(new BigDecimal(hashMapPie.get(app) / sumCount).multiply(new BigDecimal(100)).setScale(1, 0).toString());
                pieChartData.setDuration(new BigDecimal(hashMapPie.get(app)).multiply(new BigDecimal(configMap.get("MINUTE_BY_ONE_CLICK").toString())).divide(new BigDecimal(day).multiply(new BigDecimal(sumPeople)), 1, RoundingMode.HALF_UP).setScale(1, 0).toPlainString());
            }
            bigDecimal = bigDecimal.add(new BigDecimal(hashMapPie.get(app) / sumCount).multiply(new BigDecimal(100)).setScale(1, 0));
            bigDecimalTime = bigDecimalTime.add(new BigDecimal(pieChartData.getDuration()));
            pieChartData.setAppTag(app);
            pieChartDatas.add(pieChartData);
            n++;
        }
        /**
         * 结果根据时间排序
         */
        unrelatedTimess.sort(Comparator.comparing(UnrelatedTimes::getHour));

        UnrelatedTimeAndPie unrelatedTimeAndPie = new UnrelatedTimeAndPie();
        unrelatedTimeAndPie.setPieChartDataList(pieChartDatas);
        unrelatedTimeAndPie.setUnrelatedTimeList(unrelatedTimess);

        return unrelatedTimeAndPie;
    }


    private String handlePercent(String num, String day, String minutesByOneClick) {
        BigDecimal bigDecimal = new BigDecimal(num);
        //查询公司在岗人数
        Integer sumPeople = baseDao.totalNumberOfEmployeesOnGuard();

        return bigDecimal.divide(new BigDecimal(sumPeople), 4).divide(new BigDecimal(day), 4).multiply(new BigDecimal(minutesByOneClick)).setScale(1, 1).toString();
    }

    /**
     * 柱状图功能数据分装
     *
     * @param hashMapData 元数据
     * @return 目标数据
     */
    private PieChartDatas getPieChart(HashMap<String, String> hashMapData) {
        Set<String> tagStr = hashMapData.keySet();
        PieChartDatas pieChartDatas = new PieChartDatas();
        ArrayList<PieChartData> pieChartDataList = new ArrayList<>();
        for (String tag : tagStr) {
            String score = hashMapData.get(tag);
            PieChartData pieChartData = new PieChartData();
            pieChartData.setAppTag(tag);
            pieChartData.setRatio(score);
            pieChartDataList.add(pieChartData);
        }
        pieChartDatas.setPieChartDataList(pieChartDataList);
        return pieChartDatas;
    }

    private <T extends NonWorkingSoft> HashMap<String, ArrayList<T>> groupByArrayByTime
            (ArrayList<T> arrayList) {
        HashMap<String, ArrayList<T>> hashMap = new HashMap<>(16);
        for (T t : arrayList) {
            if (hashMap.get(t.getTime()) == null) {
                hashMap.put(t.getTime(), new ArrayList<>(Arrays.asList(t, t)));
            } else {
                hashMap.get(t.getTime()).add(t);
            }
        }
        return hashMap;
    }

    private <T extends NonWorkingSoft> HashMap<String, ArrayList<T>> groupByArrayByClass
            (ArrayList<T> arrayList) {
        HashMap<String, ArrayList<T>> hashMap = new HashMap<>(16);
        for (T t : arrayList) {
            //过滤多个ip属于同一个人
//            if (hashMapNumberAndIp.containsValue(t.getIp())) {
            if (hashMap.get(t.getApp_Class()) == null) {
                hashMap.put(t.getApp_Class(), new ArrayList<>(Collections.singletonList(t)));
            } else {
                hashMap.get(t.getApp_Class()).add(t);
            }
//            }
        }
        return hashMap;
    }

    private <T extends NonWorkingSoft> HashMap<String, HashMap<String, ArrayList<T>>> groupByHash
            (HashMap<String, ArrayList<T>> hashMap) {
        HashMap<String, HashMap<String, ArrayList<T>>> hashMapHashMap = new HashMap<>(16);
        Set<String> strings = hashMap.keySet();
        for (String key : strings) {
            ArrayList<T> ts = hashMap.get(key);
            hashMapHashMap.put(key, groupByArrayByClass(ts));
        }
        return hashMapHashMap;
    }


    private List<Object[]> wrapEsResult(SearchResponse sr) {
        List<Object[]> wrapResult = new ArrayList<>();

        Histogram histograms = sr.getAggregations().get("startTime");
        for (Histogram.Bucket histogram : histograms.getBuckets()) {
            String date = histogram.getKeyAsString();
            Terms appTagCollections = histogram.getAggregations().get("app_tag_collection");
            for (Terms.Bucket appTag : appTagCollections.getBuckets()) {
                String app = appTag.getKey().toString();
                Terms sourceIPs = appTag.getAggregations().get("sourceIP");
                for (Terms.Bucket sourceIP : sourceIPs.getBuckets()) {
                    String iP = sourceIP.getKey().toString();
                    ValueCount count = sourceIP.getAggregations().get("count");
                    long value = count.getValue();
                    wrapResult.add(new Object[]{date, app, iP, value});
                }
            }
        }
        return wrapResult;
    }

    private String countOther(HashMap<String, String> hashMap) {
        Set<String> scores = hashMap.keySet();
        int sum = 0;
        for (String score : scores) {
            if (!score.equals("其他")) {
                int scor = Integer.parseInt(hashMap.get(score));
                sum += scor;
            }
        }
        return String.valueOf(100 - sum);
    }


}
