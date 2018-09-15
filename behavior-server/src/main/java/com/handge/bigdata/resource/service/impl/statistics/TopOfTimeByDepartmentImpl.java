package com.handge.bigdata.resource.service.impl.statistics;

import com.handge.bigdata.UnifiedException;
import com.handge.bigdata.dao.ProxyFactory;
import com.handge.bigdata.dao.SQLBuilder;
import com.handge.bigdata.dao.api.IBaseDAO;
import com.handge.bigdata.dao.proxy.EsProxy;
import com.handge.bigdata.dao.proxy.MySQLProxy;
import com.handge.bigdata.enumeration.DAOProxyEnum;
import com.handge.bigdata.enumeration.DateFormatEnum;
import com.handge.bigdata.enumeration.ESIndexEnum;
import com.handge.bigdata.resource.models.request.statistics.TimeByDepartmentDetailParam;
import com.handge.bigdata.resource.models.request.statistics.TopOfTimeByDepartmentParam;
import com.handge.bigdata.resource.models.response.statistics.NonWorkingTimeByDepartment;
import com.handge.bigdata.resource.service.api.statistics.ITopOfTimeByDepartment;
import com.handge.bigdata.utils.CollectionUtils;
import com.handge.bigdata.utils.DateUtil;
import com.handge.bigdata.utils.FormulaUtil;
import com.handge.bigdata.utils.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scala.math.Ordering;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 工作无关部门上网人均时长TOP(统计)
 *
 * @author liuqian
 */
@Component
public class TopOfTimeByDepartmentImpl implements ITopOfTimeByDepartment {
    // TODO: 2018/7/9 liuqian 角色判断
    /**
     * 基础数据库查询bean
     */
    @Autowired
    private IBaseDAO baseDAO;

    @Override
    public Object listTopOfTimeByDepartment(TopOfTimeByDepartmentParam topOfTimeByDepartmentParam) {
        //如果没有指定周期，默认周期当前月一号到目前日期
        Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat(DateFormatEnum.SECONDS.getFormat());
        if (!StringUtils.notEmpty(topOfTimeByDepartmentParam.getStartTime())) {
            String yearAndMonthStr = df.format(day).substring(0,8);
            String dateStr = yearAndMonthStr + "01 00:00:00";
            topOfTimeByDepartmentParam.setStartTime(dateStr);
        }
        if (!StringUtils.notEmpty(topOfTimeByDepartmentParam.getEndTime())) {
            topOfTimeByDepartmentParam.setEndTime(df.format(day));
        }
        List<NonWorkingTimeByDepartment> result = getResults(topOfTimeByDepartmentParam.getStartTime(), topOfTimeByDepartmentParam.getEndTime());
        //返回top n
        return (topOfTimeByDepartmentParam.getN() <= result.size() ? result.subList(0, topOfTimeByDepartmentParam.getN()) : result);
    }

    @Override
    public Object listTimeByDepartmentDetail(TimeByDepartmentDetailParam timeByDepartmentDetailParam) {
        //如果没有指定周期，默认周期当前月一号到目前日期
        Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat(DateFormatEnum.SECONDS.getFormat());
        if (!StringUtils.notEmpty(timeByDepartmentDetailParam.getStartTime())) {
            String yearAndMonthStr = df.format(day).substring(0,8);
            String dateStr = yearAndMonthStr + "01 00:00:00";
            timeByDepartmentDetailParam.setStartTime(dateStr);
        }
        if (!StringUtils.notEmpty(timeByDepartmentDetailParam.getEndTime())) {
            timeByDepartmentDetailParam.setEndTime(df.format(day));
        }
        //返回分页数据
        List<NonWorkingTimeByDepartment> result = getResults(timeByDepartmentDetailParam.getStartTime(), timeByDepartmentDetailParam.getEndTime());
        return CollectionUtils.getPageResult(result, timeByDepartmentDetailParam.getPageNo(), timeByDepartmentDetailParam.getPageSize());
    }

    /**
     * 查询es，获取内网ip及访问次数
     *
     * @param startTime 周期开始时间
     * @param endTime   周期结束时间
     * @return Map：ipFrequencyMap
     */
    private Map<String, Long> searchForEs(String startTime, String endTime) {
        //获取配置
        Map<String, Object> configParamMap = baseDAO.getConfigParam();
        //转换成时间戳
        long startStamp = 0;
        try {
            startStamp = DateUtil.dateToTimeStamp(startTime, DateFormatEnum.SECONDS);
        } catch (ParseException e) {
            throw new UnifiedException(e);
        }
        long endStamp = 0;
        try {
            endStamp = DateUtil.dateToTimeStamp(endTime, DateFormatEnum.SECONDS);
        } catch (ParseException e) {
            throw new UnifiedException(e);
        }

        List<String> nonWorkingTagList = baseDAO.listTagsOfNonWorking();
        Map<String, Long> ipFrequencyMap = new HashMap<>();

        EsProxy esProxy = ProxyFactory.createProxy(DAOProxyEnum.ES);
        //索引
        String[] indices = esProxy.generateIndices(startTime, endTime, ESIndexEnum.MAPPING);
        TransportClient esClient = esProxy.getClient();
        String esType = configParamMap.get("ES_TYPE").toString();
        //非工作性上网sourceIP、count
        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .filter(QueryBuilders
                        .rangeQuery("startTime")
                        .from(startStamp, true)
                        .to(endStamp, true)
                )
                .filter(QueryBuilders.termsQuery("appTags", nonWorkingTagList));

        TermsAggregationBuilder aggregation = AggregationBuilders.terms("ipCount").field("localIp").size(10000);
        SearchRequestBuilder srb = esClient.prepareSearch(indices)
                .setTypes(esType)
                .setSearchType(SearchType.QUERY_AND_FETCH)
                .setQuery(queryBuilder)
                .addAggregation(aggregation);

        SearchResponse response = esProxy.action(srb);
        Map<String, Aggregation> map = response.getAggregations().asMap();
        StringTerms terms = (StringTerms) map.get("ipCount");

        for (Terms.Bucket bucket : terms.getBuckets()) {
            String sourceIp = (String) bucket.getKey();
            long countSourceIp = bucket.getDocCount();
            ipFrequencyMap.put(sourceIp, countSourceIp);
        }
        //返回连接
        esProxy.returnClient();
        return ipFrequencyMap;
    }

    /**
     * 查询mysql,获取部门及部门访问总次数
     *
     * @param startTime 周期开始时间
     * @param endTime   周期结束时间
     * @return Map<String,Long>
     */
    private List<Map<String, Long>> searchForMySql(String startTime, String endTime) {
        List<Map<String, Long>> result = new ArrayList<>();
        Map<String, Long> ipFrequencyMap = searchForEs(startTime, endTime);
        Map<String, Long> departmentTotalNumMap = new HashMap<>();
        Map<String, ArrayList<String>> departmentIpsMap = new HashMap<>();
        Map<String, Long> departmentFrequencyMap = new HashMap<>();

        MySQLProxy mySqlProxy = ProxyFactory.createProxy(DAOProxyEnum.MySQL);

        //获取部门名称、部门员工总数、部门ip集合
        String sql = "SELECT t4.department_name,COUNT(DISTINCT(t3.id)) AS department_num\n" +
                "FROM entity_device_basic t1\n" +
                "LEFT JOIN auth_account t2 ON t1.account_id = t2.id\n" +
                "LEFT JOIN entity_employee_information_basic t3 ON t2.employee_id = t3.id\n" +
                "LEFT JOIN entity_department_information_basic t4 ON t3.department_id = t4.department_id\n" +
                "WHERE t3.leave_date is NULL\n" +
                "GROUP BY t4.department_name";
        String mySql1 = SQLBuilder.sql(sql).toString();
        ResultSet rsMySql = mySqlProxy.queryBySQL(mySql1);
        try {
            while (rsMySql.next()) {
                String departmentName = rsMySql.getString(1);
                Long departmentNum = rsMySql.getLong(2);
                //存储 部门--部门员工总数
                departmentTotalNumMap.put(departmentName, departmentNum);
            }
        } catch (SQLException e) {
            throw new UnifiedException(e);
        }
        //获取 部门--部门ip集合
        departmentIpsMap = baseDAO.getEmployeeIps(null);
        //得到 部门-次数的map
        //遍历 部门--部门ip集合的map
        for (Map.Entry<String, ArrayList<String>> entry1 : departmentIpsMap.entrySet()) {
            //遍历ip--次数map集合
            for (Map.Entry<String, Long> entry2 : ipFrequencyMap.entrySet()) {
                //获取各部门ip集合
                List<String> ipList = entry1.getValue();
                //获取ip--次数map中的key值
                String ip = entry2.getKey();
                //判断部门ip集合中是否存在该key
                //如果存在，获取值（即该ip对应的次数），存入 部门-次数 的map中
                if (ipList.contains(ip)) {
                    //判断部门-次数map中是否存在该部门的统计值
                    //如果存在，将值取出来更新之后再覆盖
                    String departmentName = entry1.getKey();
                    Long deparmentFrequecncy = entry2.getValue();
                    if (departmentFrequencyMap.containsKey(departmentName)) {
                        //存在，更新该值
                        Long frequency = departmentFrequencyMap.get(departmentName);
                        Long newFrequency = frequency + deparmentFrequecncy;
                        departmentFrequencyMap.put(departmentName, newFrequency);
                    } else {
                        //不存在，直接添加
                        departmentFrequencyMap.put(departmentName, deparmentFrequecncy);
                    }
                }
            }
        }
        result.add(departmentTotalNumMap);
        result.add(departmentFrequencyMap);
        return result;
    }

    /**
     * 获取非工作性上网部门排名
     *
     * @param startTime 周期开始时间
     * @param endTime   周期结束时间
     * @return List<NonWorkingTimeByDepartment>
     */
    private List<NonWorkingTimeByDepartment> getResults(String startTime, String endTime) {
        List<NonWorkingTimeByDepartment> result = new ArrayList<>();
        List<Map<String, Long>> maps = searchForMySql(startTime, endTime);
        Map<String, Long> departmentFrequencyMap = maps.get(1);
        Map<String, Long> departmentTotalNumMap = maps.get(0);
        TreeMap<String, Long> departmentAvgFrequencyMap = new TreeMap<>();
        //部门每人每天平均时长
        for (Map.Entry<String, Long> entry : departmentFrequencyMap.entrySet()) {
            String departmentName = entry.getKey();
            Long totalFrequency = entry.getValue();

            Long avgFrequency = totalFrequency / departmentTotalNumMap.get(departmentName);
            Long avgFrequencyPerDay = 0L;
            try {
                avgFrequencyPerDay = avgFrequency/DateUtil.differentDays(startTime,endTime,DateFormatEnum.SECONDS);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            departmentAvgFrequencyMap.put(departmentName, avgFrequencyPerDay);
        }
        //将部门—次数TreeMap按value降序排列
        // 将map.entrySet()转换成list
        List<Map.Entry<String, Long>> list = new ArrayList<Map.Entry<String, Long>>(departmentAvgFrequencyMap.entrySet());
        // 通过比较器来实现排序
        Collections.sort(list, new Comparator<Map.Entry<String, Long>>() {
            @Override
            public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                // 降序排序
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        NonWorkingTimeByDepartment nonWorkingTimeByDepartment;
        Map<String, Object> configParamMap = baseDAO.getConfigParam();
        double MINUTE_BY_ONE_CLICK = Double.parseDouble(String.valueOf(configParamMap.get("MINUTE_BY_ONE_CLICK")));
        for (Map.Entry<String, Long> entry : list) {
            String dname = entry.getKey();
            nonWorkingTimeByDepartment = new NonWorkingTimeByDepartment();
            //部门
            nonWorkingTimeByDepartment.setDepartment(dname);
            //部门人数
            nonWorkingTimeByDepartment.setDepartmentNum(departmentTotalNumMap.get(dname).toString());
            //部门总时长
            Long departmentTotalDrequency = departmentFrequencyMap.get(dname);
            String departmentTotalTime = FormulaUtil.timesToHourMinute(departmentTotalDrequency,String.valueOf(MINUTE_BY_ONE_CLICK));
            nonWorkingTimeByDepartment.setTimeByDepartment(departmentTotalTime);
            //部门人均每天时长
            Long departmentAvgDrequency = entry.getValue();
            String departmentAvgTime = FormulaUtil.timesToHourMinute(departmentAvgDrequency,String.valueOf(MINUTE_BY_ONE_CLICK));
            nonWorkingTimeByDepartment.setTimeByPerson(departmentAvgTime);
            result.add(nonWorkingTimeByDepartment);
        }
        return result;
    }
}
