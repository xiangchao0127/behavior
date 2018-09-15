package com.handge.bigdata.resource.service.impl.statistics;

import com.handge.bigdata.UnifiedException;
import com.handge.bigdata.dao.ProxyFactory;
import com.handge.bigdata.dao.api.IBaseDAO;
import com.handge.bigdata.dao.proxy.EsProxy;
import com.handge.bigdata.enumeration.DAOProxyEnum;
import com.handge.bigdata.enumeration.DateFormatEnum;
import com.handge.bigdata.enumeration.ESIndexEnum;
import com.handge.bigdata.enumeration.TimeSectionEnum;
import com.handge.bigdata.resource.models.request.statistics.NonWorkingTrendParam;
import com.handge.bigdata.resource.models.response.statistics.NonWorkingTrend;
import com.handge.bigdata.resource.service.api.statistics.INonWorkingTrend;
import com.handge.bigdata.utils.DateUtil;
import com.handge.bigdata.utils.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filters.Filters;
import org.elasticsearch.search.aggregations.bucket.filters.FiltersAggregator;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/**
 * 工作无关上网趋势
 *
 * @author LiuJihao
 * @date 2018/05/08
 */
@SuppressWarnings("Duplicates")
@Component
public class NonWorkingTrendImpl implements INonWorkingTrend {

    /**
     * 基础数据库查询Bean
     */
    @Autowired
    IBaseDAO BASE_DAO;

    Log logger = LogFactory.getLog(this.getClass());

    /**
     * 默认工作无关上网阈值（每天最少分钟数/天）
     */
    private double DEFAULT_NON_WORKING_THRESHOLD = 0;

    /**
     * 默认查询时间类型
     */
    private TimeSectionEnum DEFAULT_TIME_SECTION = TimeSectionEnum.MONTH_SECTION;

    private static String KEY_NON_WORKING = "nonWorking";
    private static String KEY_WORKING = "working";
    private static Long TIME_ZONE_OFFSET = 8 * 60 * 60 * 1000L;

    @Override
    public Object listNonWorkingTrend(NonWorkingTrendParam nonWorkingTrendParam) {

        /**
         * 按传参修改默认值
         */
        DEFAULT_NON_WORKING_THRESHOLD = nonWorkingTrendParam.getThreshold();
        DEFAULT_TIME_SECTION = nonWorkingTrendParam.getSection();

        //获取配置
        Map<String, Object> configParamMap = BASE_DAO.getConfigParam();
        /**
         * 1.通过ES函数查询数据
         */
        SearchResponse sr = searchForES(nonWorkingTrendParam, configParamMap);

        /**
         * 2.将ES结果进行Flat操作
         */
        List<Object[]> esResult = wrapEsResult(sr);

        /**
         * 3.针对多个IP进行Reduce操作
         */
        Map<String, Map<String, Map<String, Long>>> reduceMap = reduceByPersonal(esResult);

        /**
         * 4.封装到前端DTO中
         */
        List<NonWorkingTrend> result = transformToFrontResult(reduceMap, configParamMap);

        return result;
    }

    private SearchResponse searchForES(NonWorkingTrendParam param, Map<String, Object> configParamMap) {
        String date = param.getDate();
        String departmentName = param.getDepartment();

        List<String> nonWorkingTags = BASE_DAO.listTagsOfNonWorking();
        List<String> workingTags = BASE_DAO.listTagsOfWorking();

        TimeSectionEnum section = param.getSection();

        long start;
        long end;
        Date[] indexRange = new Date[2];
        DateHistogramInterval dateHistogramInterval = null;
        Date[] points = new Date[2];
        try {
            if (section == null || section.equals(TimeSectionEnum.MONTH_SECTION)) {
                points = getTimePointBetweenYear(date);
                indexRange = DateUtil.getStartAndEndByYear(date);
                dateHistogramInterval = DateHistogramInterval.MONTH;
            } else if (section.equals(TimeSectionEnum.DAY_SECTION)) {
                points = getTimePointBetweenMonth(date);
                indexRange = new Date[]{DateUtil.str2Date(DateFormatEnum.SECONDS, date), DateUtil.str2Date(DateFormatEnum.SECONDS, date)};
                dateHistogramInterval = DateHistogramInterval.DAY;
            } else if (section.equals(TimeSectionEnum.WEEK_SECTION)) {
                points = getTimePointBetweenMonth(date);
                indexRange = new Date[]{DateUtil.str2Date(DateFormatEnum.SECONDS, date), DateUtil.str2Date(DateFormatEnum.SECONDS, date)};
                dateHistogramInterval = DateHistogramInterval.WEEK;
            }
            start = points[0].getTime();
            end = points[1].getTime();
        } catch (Exception e) {
            throw new UnifiedException(e);
        }

        EsProxy esProxy = ProxyFactory.createProxy(DAOProxyEnum.ES);
        String[] indices = esProxy.generateIndices(indexRange[0], indexRange[1], ESIndexEnum.MAPPING);
        TransportClient esClient = esProxy.getClient();
        String esType = configParamMap.get("ES_TYPE").toString();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .filter(QueryBuilders.rangeQuery("startTime").from(start, true).to(end, false));
        if (StringUtils.notEmpty(departmentName)) {
            ArrayList<String> deptIps = BASE_DAO.getEmployeeIps(departmentName).get(departmentName);
            queryBuilder.filter(QueryBuilders.termsQuery("localIp", deptIps));
        }
        SearchRequestBuilder builder;

        builder = esClient.prepareSearch(indices)
                .setTypes(esType)
                .setQuery(queryBuilder)
                .addAggregation(AggregationBuilders.dateHistogram("date").field("startTime").dateHistogramInterval(dateHistogramInterval)
                        .subAggregation(AggregationBuilders.terms("sourceIP").field("localIp").size(1000)
                                .subAggregation(AggregationBuilders.filters("workingReference",
                                        new FiltersAggregator.KeyedFilter("nonWorking", QueryBuilders.termsQuery("appTags", nonWorkingTags)),
                                        new FiltersAggregator.KeyedFilter("working", QueryBuilders.termsQuery("appTags", workingTags))))
                        )
                );

        logger.debug(builder);

        SearchResponse sr = esProxy.action(builder);

        esProxy.returnClient();

        return sr;
    }

    private List<Object[]> wrapEsResult(SearchResponse sr) {
        List<Object[]> wrapResult = new ArrayList<>();

        Histogram histograms = sr.getAggregations().get("date");
        for (Histogram.Bucket histogram : histograms.getBuckets()) {
            String date = histogram.getKeyAsString();
            Terms sourceIPs = histogram.getAggregations().get("sourceIP");
            for (Terms.Bucket sourceIP : sourceIPs.getBuckets()) {
                Filters workingReference = sourceIP.getAggregations().get("workingReference");
                Filters.Bucket nonWorking = workingReference.getBucketByKey("nonWorking");
                Filters.Bucket working = workingReference.getBucketByKey("working");
                String IP = sourceIP.getKeyAsString();
                long nonWorkingCount = nonWorking.getDocCount();
                long workingCount = working.getDocCount();
                wrapResult.add(new Object[]{date, IP, nonWorkingCount, workingCount});
            }
        }

        return wrapResult;
    }

    /**
     * 将查询结果封装为[日期：[员工编号：点击次数]]的形式，其中会对sourceIP做reduce操作合并为相同员工，次数累加
     *
     * @param
     * @return java.util.Map<java.lang.String,java.util.Map<java.lang.String,java.lang.Double>>
     * @author LiuJihao
     * @date 2018/5/7 18:49
     **/
    private Map<String, Map<String, Map<String, Long>>> reduceByPersonal(List<Object[]> resultSet) {
        Map<String, Map<String, Map<String, Long>>> timeSourceIpCountMap = new HashMap<>(14);
        Map<String, String> ipNumberMap = BASE_DAO.getAllEmployeeIpAndNumber();
        for (Object[] obj : resultSet) {
            long dateInit = Long.parseLong(obj[0].toString()) + TIME_ZONE_OFFSET;
            String date = null;
            if (TimeSectionEnum.MONTH_SECTION.equals(DEFAULT_TIME_SECTION)) {
                date = DateUtil.timeStampToStrDate(dateInit, DateFormatEnum.MONTH);
            } else if (TimeSectionEnum.DAY_SECTION.equals(DEFAULT_TIME_SECTION) || TimeSectionEnum.WEEK_SECTION.equals(DEFAULT_TIME_SECTION)) {
                date = DateUtil.timeStampToStrDate(dateInit, DateFormatEnum.DAY);
            }
            String sourceIp = (String) obj[1];
            long nonWorkingCount = (long) obj[2];
            long workingCount = (long) obj[3];
            String employeeNumber = ipNumberMap.get(sourceIp);
            if (employeeNumber == null) {
                continue;
            }
            if (timeSourceIpCountMap.containsKey(date)) {
                if (timeSourceIpCountMap.get(date).containsKey(employeeNumber)) {
                    long newNonWorkingCount = timeSourceIpCountMap.get(date).get(employeeNumber).get(KEY_NON_WORKING) + nonWorkingCount;
                    long newWorkingCount = timeSourceIpCountMap.get(date).get(employeeNumber).get(KEY_WORKING) + workingCount;
                    timeSourceIpCountMap.get(date).get(employeeNumber).put(KEY_NON_WORKING, newNonWorkingCount);
                    timeSourceIpCountMap.get(date).get(employeeNumber).put(KEY_WORKING, newWorkingCount);
                } else {
                    Map<String, Long> countMap = new HashMap<>(2);
                    countMap.put(KEY_NON_WORKING, nonWorkingCount);
                    countMap.put(KEY_WORKING, workingCount);
                    timeSourceIpCountMap.get(date).put(employeeNumber, countMap);
                }
            } else {
                Map<String, Map<String, Long>> sourceIpCountMap = new HashMap<>(ipNumberMap.size());
                Map<String, Long> countMap = new HashMap<>(2);
                countMap.put(KEY_NON_WORKING, nonWorkingCount);
                countMap.put(KEY_WORKING, workingCount);
                sourceIpCountMap.put(employeeNumber, countMap);
                timeSourceIpCountMap.put(date, sourceIpCountMap);
            }
        }

        return timeSourceIpCountMap;
    }

    private List<NonWorkingTrend> transformToFrontResult(Map<String, Map<String, Map<String, Long>>> map, Map<String, Object> configParamMap) {

        List<NonWorkingTrend> result = new ArrayList<>();

        Map<String, Map<String, Map<String, Long>>> reduceMap = map;
        //生效分钟
        double MINUTE_BY_ONE_CLICK = Double.parseDouble(configParamMap.get("MINUTE_BY_ONE_CLICK").toString());
        //公司实际在岗总人数
        int numberOfEmployeesOnGuard = BASE_DAO.totalNumberOfEmployeesOnGuard();

        for (Map.Entry<String, Map<String, Map<String, Long>>> entry : reduceMap.entrySet()) {
            double totalTimeByDate = 0;
            double totalWorkTimeByDate = 0;
            int actualPerson = entry.getValue().size();
            for (Map.Entry<String, Map<String, Long>> set : entry.getValue().entrySet()) {
                //计算阈值：
                double thresholdOfMonth = countWorkingDayForMonth(entry.getKey(), true) * this.DEFAULT_NON_WORKING_THRESHOLD;
                boolean thresholdNotZero = thresholdOfMonth != 0;
                //不满足阈值时：
                if (thresholdNotZero && (set.getValue().get(KEY_NON_WORKING) < thresholdOfMonth)) {
                    actualPerson--;
                    continue;
                }
                totalTimeByDate += (set.getValue().get(KEY_NON_WORKING) * MINUTE_BY_ONE_CLICK);
                totalWorkTimeByDate += (set.getValue().get(KEY_WORKING) * MINUTE_BY_ONE_CLICK);
            }
            String numOfPerson = actualPerson + "";
            String date = entry.getKey();
            String ratioOfPerson = new BigDecimal(numOfPerson)
                    .divide(new BigDecimal(numberOfEmployeesOnGuard), 2, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal(100))
                    .setScale(1, BigDecimal.ROUND_HALF_UP)
                    .toPlainString();
            String nonWorkingTime = new BigDecimal(totalTimeByDate)
                    .divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP)
                    .divide(new BigDecimal(numberOfEmployeesOnGuard), 2, BigDecimal.ROUND_HALF_UP)
                    .setScale(1, BigDecimal.ROUND_HALF_UP)
                    .toPlainString();

            String workingTime = new BigDecimal(totalWorkTimeByDate)
                    .divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP)
                    .divide(new BigDecimal(numberOfEmployeesOnGuard), 2, BigDecimal.ROUND_HALF_UP)
                    .setScale(1, BigDecimal.ROUND_HALF_UP)
                    .toPlainString();

            NonWorkingTrend nonWorkingTrend = new NonWorkingTrend();
            if (TimeSectionEnum.MONTH_SECTION.equals(DEFAULT_TIME_SECTION)) {
                nonWorkingTrend.setMonth(date);
            } else if (TimeSectionEnum.DAY_SECTION.equals(DEFAULT_TIME_SECTION)) {
                nonWorkingTrend.setDay(date);
            } else if (TimeSectionEnum.WEEK_SECTION.equals(DEFAULT_TIME_SECTION)) {
                nonWorkingTrend.setWeek(date);
            }
            nonWorkingTrend.setNumOfPerson(numOfPerson);
            nonWorkingTrend.setRatioOfPerson(ratioOfPerson);
            nonWorkingTrend.setNonWorkingTime(nonWorkingTime);

            nonWorkingTrend.setWorkingTime(workingTime);
            result.add(nonWorkingTrend);
        }
        sortByDate(result, DEFAULT_TIME_SECTION);

        return result;
    }

    private void sortByDate(List<NonWorkingTrend> list, TimeSectionEnum section) {
        if (TimeSectionEnum.MONTH_SECTION.equals(section)) {
            list.sort(Comparator.comparing(NonWorkingTrend::getMonth));
        } else if (TimeSectionEnum.DAY_SECTION.equals(section)) {
            list.sort(Comparator.comparing(NonWorkingTrend::getDay));
        } else if (TimeSectionEnum.WEEK_SECTION.equals(section)) {
            list.sort(Comparator.comparing(NonWorkingTrend::getWeek));
        }
    }

    /**
     * 获取该月的工作日天数
     *
     * @param
     * @return int
     * @author LiuJihao
     * @date 2018/5/8 9:06
     **/
    public int countWorkingDayForMonth(String date, boolean isAll) {
        int count = 0;
        int month = Integer.parseInt(date.substring(5, 7));
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(date.substring(0, 4)));
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DATE, 1);
        if (isAll) {
            count = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        } else {
            while (cal.get(Calendar.MONTH) < month) {
                int day = cal.get(Calendar.DAY_OF_WEEK);

                if (!(day == Calendar.SUNDAY || day == Calendar.SATURDAY)) {
                    count++;
                }

                cal.add(Calendar.DATE, 1);
            }
        }
        return count;
    }

    /**
     * 获取距离当前已发生的十二个月的起始时间点和结束时间点
     *
     * @param
     * @return java.lang.Object[], 其中obj[0]为起始时间obj[1]为结束时间
     * @author LiuJihao
     * @date 2018/5/6 16:59
     **/
    private Date[] getTimePointBetween12Month() {
        String startStr = DateUtil.date2Str(new Date(), DateFormatEnum.MONTH);
        Date start = null;
        Date end = null;
        try {
            end = DateUtil.str2Date(DateFormatEnum.MONTH, startStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(end);
        calendar.add(Calendar.MONTH, -12);
        start = calendar.getTime();
        Date[] points = {start, new Date()};
        return points;
    }

    /**
     * 获取传入时间所在年份的起始时间点和结束时间点
     *
     * @param
     * @return java.lang.Object[], 其中obj[0]为起始时间obj[1]为结束时间
     * @author LiuJihao
     * @date 2018/5/6 16:59
     **/
    private Date[] getTimePointBetweenYear(String time) {
        Date timeDate;
        Date start = null;
        Date end = null;
        try {
            timeDate = DateUtil.str2Date(DateFormatEnum.MONTH, time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(timeDate);
            int startYear = calendar.get(Calendar.YEAR);
            int endYear = startYear + 1;
            start = DateUtil.str2Date(DateFormatEnum.YEAR, String.valueOf(startYear));
            end = DateUtil.str2Date(DateFormatEnum.YEAR, String.valueOf(endYear));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date[] points = {start, end};
        return points;
    }

    /**
     * 获取传入时间所在月份的起始时间点和结束时间点
     *
     * @param
     * @return java.lang.Object[], 其中obj[0]为起始时间obj[1]为结束时间
     * @author LiuJihao
     * @date 2018/5/6 16:59
     **/
    private Date[] getTimePointBetweenMonth(String time) {
        Date timeDate;
        Date start = null;
        Date end = null;
        try {
            timeDate = DateUtil.str2Date(DateFormatEnum.MONTH, time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(timeDate);
            start = timeDate;
            calendar.add(Calendar.MONTH, 1);
            end = calendar.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date[] points = {start, end};
        return points;
    }
}