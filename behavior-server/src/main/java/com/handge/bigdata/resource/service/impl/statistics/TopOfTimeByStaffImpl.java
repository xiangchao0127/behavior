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
import com.handge.bigdata.enumeration.ExceptionWrapperEnum;
import com.handge.bigdata.resource.models.request.statistics.TimeByStaffDetailParam;
import com.handge.bigdata.resource.models.request.statistics.TopOfTimeByStaffParam;
import com.handge.bigdata.resource.models.response.statistics.NonWorkingTimeByStaff;
import com.handge.bigdata.resource.service.api.statistics.ITopOfTimeByStaff;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 工作无关部门员工上网时长TOP（统计）
 *
 * @author liuqian
 */
@Component
public class TopOfTimeByStaffImpl implements ITopOfTimeByStaff {
    // TODO: 2018/5/10 liuqian 角色判断

    /**
     * 基础数据库查询bean
     */
    @Autowired
    private IBaseDAO baseDAO;

    @Override
    public Object listTopOfTimeByStaff(TopOfTimeByStaffParam topOfTimeByStaffParam) {
        //如果没有指定周期，默认周期当前月一号到目前日期
        Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat(DateFormatEnum.SECONDS.getFormat());
        if (!StringUtils.notEmpty(topOfTimeByStaffParam.getStartTime())) {
            String yearAndMonthStr = df.format(day).substring(0,8);
            String dateStr = yearAndMonthStr + "01 00:00:00";
            topOfTimeByStaffParam.setStartTime(dateStr);
        }
        if (!StringUtils.notEmpty(topOfTimeByStaffParam.getEndTime())) {
            topOfTimeByStaffParam.setEndTime(df.format(day));
        }
        List<NonWorkingTimeByStaff> result = getResults(topOfTimeByStaffParam.getStartTime(), topOfTimeByStaffParam.getEndTime(),topOfTimeByStaffParam);
        //返回top n
        return (topOfTimeByStaffParam.getN() <= result.size() ? result.subList(0, topOfTimeByStaffParam.getN()) : result);
    }

    @Override
    public Object listTimeByStaffDetail(TimeByStaffDetailParam timeByStaffDetailParam) {
        //如果没有指定周期，默认周期当前月一号到目前日期
        Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat(DateFormatEnum.SECONDS.getFormat());
        if (!StringUtils.notEmpty(timeByStaffDetailParam.getStartTime())) {
            String yearAndMonthStr = df.format(day).substring(0,8);
            String dateStr = yearAndMonthStr + "01 00:00:00";
            timeByStaffDetailParam.setStartTime(dateStr);
        }
        if (!StringUtils.notEmpty(timeByStaffDetailParam.getEndTime())) {
            timeByStaffDetailParam.setEndTime(df.format(day));
        }
        List<NonWorkingTimeByStaff> result = getResults(timeByStaffDetailParam.getStartTime(), timeByStaffDetailParam.getEndTime(),timeByStaffDetailParam);
        //返回分页数据
        return CollectionUtils.getPageResult(result, timeByStaffDetailParam.getPageNo(), timeByStaffDetailParam.getPageSize());
    }

    /**
     * 查询es,获取ip及该ip的访问次数
     *
     * @param startTime 周期开始时间
     * @param endTime   周期结束时间
     * @return Map<String,Long>
     */
    private Map<String, Long> searchForEs(String startTime, String endTime,Object o) {
        //获取配置
        Map<String, Object> configParamMap = baseDAO.getConfigParam();
        //周期时间戳
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

        Map<String, Long> ipFrequencyMap = new HashMap<>();
        List<String> nonWorkingTagList = baseDAO.listTagsOfNonWorking();

        EsProxy esProxy = ProxyFactory.createProxy(DAOProxyEnum.ES);
        String[] indices = esProxy.generateIndices(startTime, endTime, ESIndexEnum.MAPPING);
        String esType = configParamMap.get("ES_TYPE").toString();
        TransportClient esClient = esProxy.getClient();
        //非工作性上网ip及访问次数
        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .filter(QueryBuilders
                        .rangeQuery("startTime")
                        .from(startStamp, true)
                        .to(endStamp)
                )
                .filter(QueryBuilders.termsQuery("appTags", nonWorkingTagList));
        TermsAggregationBuilder aggregation = AggregationBuilders.terms("ipCount").field("localIp").size(1000);
        SearchRequestBuilder srb = esClient.prepareSearch(indices)
                .setTypes(esType)
                .setSearchType(SearchType.QUERY_AND_FETCH)
                .setQuery(queryBuilder)
                .addAggregation(aggregation);
        SearchResponse response = esProxy.action(srb);
        Map<String, Aggregation> map = response.getAggregations().asMap();
        StringTerms terms = (StringTerms) map.get("ipCount");
        Iterator<Terms.Bucket> iterator = terms.getBuckets().iterator();

        while (iterator.hasNext()) {
            Terms.Bucket bucket = iterator.next();
            String sourceIp = (String) bucket.getKey();
            long countSourceIp = bucket.getDocCount();
            ipFrequencyMap.put(sourceIp, countSourceIp);
        }
        //返回连接
        esProxy.returnClient();
        return ipFrequencyMap;
    }

    /**
     * 查询mysql，获取员工姓名及员工编号、部门和员工姓名及该员工访问次数
     *
     * @param startTime 周期开始时间
     * @param endTime   周期结束时间
     * @return List index 0：employeeBasicMap ；index 1：employeeFrequencyMap
     */
    private List searchForMySql(String startTime, String endTime,Object o) {
        List alist = new ArrayList();
        Map<String, Long> ipFrequencyMap = searchForEs(startTime, endTime,o);
        Map<String, String> employeeIpsMap = new HashMap<>();
        MySQLProxy mySqlProxy = ProxyFactory.createProxy(DAOProxyEnum.MySQL);

        Map<String, List<String>> employeeBasicMap = new HashMap<>();
        TreeMap<String, Long> employeeFrequencyMap = new TreeMap<>();

        String depName = null;
        if(o instanceof TopOfTimeByStaffParam) {
            depName = ((TopOfTimeByStaffParam) o).getDepartment();
        } else if(o instanceof TimeByStaffDetailParam){
            depName = ((TimeByStaffDetailParam) o).getDepartment();
        }
        if(!StringUtils.notEmpty(depName)){
            throw new UnifiedException("部门参数", ExceptionWrapperEnum.NOT_NULL);
        }
        //获取员工姓名、工号、部门名称、员工ip集合
        String sql = "SELECT t3.`name`,t3.number,t4.department_name,GROUP_CONCAT(t1.`static_ip`) AS ip_list\n" +
                "FROM entity_device_basic t1\n" +
                "LEFT JOIN auth_account t2 ON t1.account_id = t2.id\n" +
                "LEFT JOIN entity_employee_information_basic t3 ON t2.employee_id = t3.id\n" +
                "LEFT JOIN entity_department_information_basic t4 ON t3.department_id = t4.department_id\n" +
                "AND t3.leave_date IS NULL\n" +
                "WHERE t4.department_name = '"+ depName +"'\n"+
                "GROUP BY t3.`name` ";
        String mySql1 = SQLBuilder.sql(sql).toString();
        ResultSet rsMySql = mySqlProxy.queryBySQL(mySql1);
        List<String> nameNumberList;
        try {
            while (rsMySql.next()) {
                String employeeName = rsMySql.getString(1);
                employeeIpsMap.put(employeeName, rsMySql.getString(4));
                //存储 员工姓名--工号、部门
                nameNumberList = new ArrayList<>();
                nameNumberList.add(rsMySql.getString(2));
                nameNumberList.add(rsMySql.getString(3));
                employeeBasicMap.put(employeeName, nameNumberList);
            }
        } catch (SQLException e) {
            throw new UnifiedException(e);
        }
        //得到员工-次数的map
        //遍历员工及ip集合的map
        for (Map.Entry<String, String> entry1 : employeeIpsMap.entrySet()) {
            for (Map.Entry<String, Long> entry2 : ipFrequencyMap.entrySet()) {
                String ips = entry1.getValue();
                List<String> ipList = Arrays.asList(ips.split(","));
                //判断ip是否存在这个集合中
                String ip = entry2.getKey();
                //如果存在，获取该ip次数，存入 员工-次数 的map中
                if (ipList.contains(ip)) {
                    //判断员工-次数map中是否存在该key
                    if (employeeFrequencyMap.containsKey(entry1.getKey())) {
                        //存在则将该Key的值取出来加上新值
                        Long frequency = employeeFrequencyMap.get(entry1.getKey());
                        Long newFrequency = frequency + entry2.getValue();
                        employeeFrequencyMap.put(entry1.getKey(), newFrequency);
                    } else {
                        //不存在，直接存
                        employeeFrequencyMap.put(entry1.getKey(), entry2.getValue());
                    }
                }
            }
        }
        alist.add(employeeBasicMap);
        alist.add(employeeFrequencyMap);
        return alist;
    }

    /**
     * 获取对象集合
     *
     * @param startTime 周期开始时间
     * @param endTime   周期结束时间
     * @return List<NonWorkingTimeByStaff>
     */
    private List<NonWorkingTimeByStaff> getResults(String startTime, String endTime,Object o) {
        List<NonWorkingTimeByStaff> result = new ArrayList<>();
        List reList = searchForMySql(startTime, endTime,o);
        Map<String, List<String>> employeeBasicMap = (Map<String, List<String>>) reList.get(0);
        TreeMap<String, Long> employeeFrequencyMap = (TreeMap<String, Long>) reList.get(1);

        //将员工—次数TreeMap按value降序排列
        // 将map.entrySet()转换成list
        List<Map.Entry<String, Long>> list = new ArrayList<Map.Entry<String, Long>>(employeeFrequencyMap.entrySet());
        // 通过比较器来实现排序
        Collections.sort(list, new Comparator<Map.Entry<String, Long>>() {
            @Override
            public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                // 降序排序
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        NonWorkingTimeByStaff nonWorkingTimeByStaff;
        Map<String, Object> configParamMap = baseDAO.getConfigParam();
        double MINUTE_BY_ONE_CLICK = Double.parseDouble(String.valueOf(configParamMap.get("MINUTE_BY_ONE_CLICK")));
        for (Map.Entry<String, Long> mapping : list) {
            String employeeName = mapping.getKey();
            nonWorkingTimeByStaff = new NonWorkingTimeByStaff();
            //员工姓名
            nonWorkingTimeByStaff.setName(employeeName);
            //上网时长
            Long employeeFrequency = 0L;
            try {
                int i = DateUtil.differentDays(startTime, endTime, DateFormatEnum.SECONDS);
                employeeFrequency = mapping.getValue()/i;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String employeeTime = FormulaUtil.timesToHourMinute(employeeFrequency,String.valueOf(MINUTE_BY_ONE_CLICK));
            nonWorkingTimeByStaff.setTimeLength(employeeTime);
            //工号
            nonWorkingTimeByStaff.setUserId(employeeBasicMap.get(employeeName).get(0));
            //部门
            nonWorkingTimeByStaff.setDepartment(employeeBasicMap.get(employeeName).get(1));
            result.add(nonWorkingTimeByStaff);
        }
        return result;
    }
}
