package com.handge.bigdata.resource.service.impl.monitor;

import com.handge.bigdata.UnifiedException;
import com.handge.bigdata.dao.ProxyFactory;
import com.handge.bigdata.dao.SQLBuilder;
import com.handge.bigdata.dao.api.IBaseDAO;
import com.handge.bigdata.dao.proxy.EsProxy;
import com.handge.bigdata.dao.proxy.MySQLProxy;
import com.handge.bigdata.enumeration.DAOProxyEnum;
import com.handge.bigdata.enumeration.ESIndexEnum;
import com.handge.bigdata.resource.models.request.monitor.NonWorkingTimeDetailParam;
import com.handge.bigdata.resource.models.request.monitor.TopOfNonWorkingTimeParam;
import com.handge.bigdata.resource.models.response.monitor.AppTime;
import com.handge.bigdata.resource.models.response.monitor.NonWorkingTimeLengthUserTop;
import com.handge.bigdata.resource.service.api.monitor.ITopOfNonWorkingTime;
import com.handge.bigdata.utils.CollectionUtils;
import com.handge.bigdata.utils.FormulaUtil;
import com.handge.bigdata.utils.StringUtils;
import com.handge.bigdata.utils.TimeUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
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
 * 工作无关上网时长Top（实时监控）
 *
 * @author liuqian
 */
@Component
public class TopOfNonWorkingTimeImpl implements ITopOfNonWorkingTime {
    // TODO: 2018/5/10 liuqian 角色判断
    /**
     * 基础数据库查询bean
     */
    @Autowired
    private IBaseDAO baseDAO;

    @Override
    public Object listTopOfNonWorkingTime(TopOfNonWorkingTimeParam topOfNonWorkingTimeParam) {
        List<NonWorkingTimeLengthUserTop> result = getResult(topOfNonWorkingTimeParam);
        //返回top n
        return (topOfNonWorkingTimeParam.getN() <= result.size() ? result.subList(0, topOfNonWorkingTimeParam.getN()) : result);
    }

    @Override
    public Object listNonWorkingTimeDetail(NonWorkingTimeDetailParam nonWorkingTimeDetailParam) {
        List<NonWorkingTimeLengthUserTop> result = getResult(nonWorkingTimeDetailParam);
        //返回分页数据
        return CollectionUtils.getPageResult(result, nonWorkingTimeDetailParam.getPageNo(), nonWorkingTimeDetailParam.getPageSize());
    }

    /**
     * 查询mysql，获取员工姓名及员工ip集合、员工姓名及部门工号
     *
     * @return List ibdex 0:employeeIpsMap; index 1:employeeBasicMap
     */
    private List searchForMySql(Object o) {
        //员工姓名-ip集合
        Map<String, String> employeeIpsMap = new HashMap<>();
        //员工姓名-工号、部门
        Map<String, List<String>> employeeBasicMap = new HashMap<>();

        StringBuilder sb = new StringBuilder();
        if(o instanceof TopOfNonWorkingTimeParam){
            String department =((TopOfNonWorkingTimeParam)o).getDepartment();
            if(StringUtils.notEmpty(department)){
                sb.append(" AND t4.department_name like '").append(((TopOfNonWorkingTimeParam)o).getDepartment()).append("'\n");
            }
        }
        if(o instanceof NonWorkingTimeDetailParam) {
            NonWorkingTimeDetailParam nonWorkingTimeDetailParam = (NonWorkingTimeDetailParam)o;
            if (StringUtils.notEmpty(nonWorkingTimeDetailParam.getNumber())) {
                sb.append(" AND t3.number = '").append(nonWorkingTimeDetailParam.getNumber()).append("'\n");
            }
            if (StringUtils.notEmpty(nonWorkingTimeDetailParam.getName())) {
                sb.append(" AND t3.`name` like '%").append(nonWorkingTimeDetailParam.getName()).append("%'").append("\n");
            }
            if (StringUtils.notEmpty(nonWorkingTimeDetailParam.getDepartment())) {
                sb.append(" AND t4.department_name like '%").append(nonWorkingTimeDetailParam.getDepartment()).append("%'").append("\n");
            }
        }
        MySQLProxy mySqlProxy = ProxyFactory.createProxy(DAOProxyEnum.MySQL);
        String sql = "SELECT t3.`name`,t3.number,t4.department_name,GROUP_CONCAT(t1.`static_ip`) AS ip_list\n" +
                "FROM entity_device_basic t1\n" +
                "LEFT JOIN auth_account t2 ON t1.account_id = t2.id\n" +
                "LEFT JOIN entity_employee_information_basic t3 ON t2.employee_id = t3.id\n" +
                "LEFT JOIN entity_department_information_basic t4 ON t3.department_id = t4.department_id\n" +
                "WHERE t3.leave_date is NULL\n" + sb +
                "GROUP BY t3.number";
        String mySql = SQLBuilder.sql(sql).toString();
        ResultSet rsMySql = mySqlProxy.queryBySQL(mySql);
        try {
            while (rsMySql.next()) {
                String employeeName = rsMySql.getString(1);
                String employeeIpList = rsMySql.getString(4);
                employeeIpsMap.put(employeeName, employeeIpList);
                //存储 员工姓名--工号、部门
                List<String> nameNumberDepartmentList;
                nameNumberDepartmentList = new ArrayList<>();
                nameNumberDepartmentList.add(rsMySql.getString(2));
                nameNumberDepartmentList.add(rsMySql.getString(3));
                employeeBasicMap.put(employeeName, nameNumberDepartmentList);
            }
        } catch (SQLException e) {
            throw new UnifiedException(e);
        }
        List list = new ArrayList();
        list.add(employeeIpsMap);
        list.add(employeeBasicMap);
        return list;
    }

    /**
     * 查询es
     *
     * @return SearchResponse
     */
    private SearchResponse searchForEs() {
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
        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .filter(QueryBuilders
                        .rangeQuery("startTime")
                        .from(zeroTimeStamp, true)
                        .to(curTimeStamp, true)
                )
                .filter(QueryBuilders.termsQuery("appTags", nonWorkingTagList));
        TermsAggregationBuilder ipAgg = AggregationBuilders.terms("ipCount").field("localIp").size(10000);
        TermsAggregationBuilder appAgg = AggregationBuilders.terms("appCount").field("appName").size(3);
        SearchRequestBuilder srb = esClient.prepareSearch(indices)
                .setTypes(esType)
                .setSearchType(SearchType.QUERY_AND_FETCH)
                .setQuery(queryBuilder)
                .addAggregation(ipAgg.subAggregation(appAgg));
        //返回连接
        esProxy.returnClient();
        return esProxy.action(srb);
    }

    /**
     * 处理es和mysql获取的数据，得到对象集合
     *
     * @return List<NonWorkingTimeLengthUserTop>
     */
    private List<NonWorkingTimeLengthUserTop> getResult(Object o) {
        List<NonWorkingTimeLengthUserTop> result = new ArrayList<>();
        Map<String, String> employeeIpsMap = null;
        Map<String, Map<String, Long>> ipAppNameCountMap = new HashMap<>();
        TreeMap<String, Long> appNameCountMap = null;
        TreeMap<String, Long> employeeFrequencyMap = new TreeMap<>();
        //员工姓名-工号、部门
        Map<String, List<String>> employeeBasicMap = new HashMap<>();
        Map<String, TreeMap<String, Long>> employeeAppFrequencyMap = new HashMap<>();

        try {
            //查询mysql
            List alist = searchForMySql(o);
            employeeIpsMap = (Map<String, String>) alist.get(0);
            employeeBasicMap = (Map<String, List<String>>) alist.get(1);
            //查询es
            SearchResponse response = searchForEs();

            Terms sourceIpCount = response.getAggregations().get("ipCount");
            for (Terms.Bucket bucket : sourceIpCount.getBuckets()) {
                String ip = bucket.getKeyAsString();
                long countSourceIp = bucket.getDocCount();
                Terms appCount = bucket.getAggregations().get("appCount");
                appNameCountMap = new TreeMap<>();
                for (Terms.Bucket apps : appCount.getBuckets()) {
                    String appName = (String) apps.getKey();
                    long count = apps.getDocCount();
                    appNameCountMap.put(appName, count);
                }
                ipAppNameCountMap.put(ip, appNameCountMap);
                for (Map.Entry<String, String> entry : employeeIpsMap.entrySet()) {
                    String empLoyeeName = entry.getKey();
                    //ip集合
                    String ips = entry.getValue();
                    List<String> ipList = Arrays.asList(ips.split(","));
                    //判断ip是否存在这个集合中
                    //如果存在，存入 员工-应用时长的map中
                    if (ipList.contains(ip)) {
                        //判断员工-次数map中是否存在该key
                        if (employeeFrequencyMap.containsKey(empLoyeeName)) {
                            //存在则将该Key的值取出来加上新值
                            Long oldFrequency = employeeFrequencyMap.get(empLoyeeName);
                            Long newFrequency = oldFrequency + countSourceIp;
                            employeeFrequencyMap.put(empLoyeeName, newFrequency);
                        } else {
                            //不存在，直接存
                            employeeFrequencyMap.put(empLoyeeName, countSourceIp);
                        }

                        //判断员工-应用次数map中是否存在该key
                        if (employeeAppFrequencyMap.containsKey(empLoyeeName)) {
                            //获取值
                            TreeMap<String, Long> appFrequency = employeeAppFrequencyMap.get(empLoyeeName);
                            for (Map.Entry<String, Long> entry1 : appFrequency.entrySet()) {
                                String appName = entry1.getKey();
                                Long count = entry1.getValue();
                                if (appNameCountMap.containsKey(appName)) {
                                    Long oldCount = appNameCountMap.get(appName);
                                    Long newCount = appFrequency.get(appName);
                                    appNameCountMap.put(appName, oldCount + newCount);
                                } else {
                                    appNameCountMap.put(appName, count);
                                }
                            }
                        } else {
                            employeeAppFrequencyMap.put(empLoyeeName, appNameCountMap);
                        }
                    }
                }
            }
            //将员工—次数TreeMap按value降序排列
            //将map.entrySet()转换成list
            List<Map.Entry<String, Long>> list = new ArrayList<Map.Entry<String, Long>>(employeeFrequencyMap.entrySet());
            // 通过比较器来实现排序
            list.sort(new Comparator<Map.Entry<String, Long>>() {
                @Override
                public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                    // 降序排序
                    return o2.getValue().compareTo(o1.getValue());
                }
            });
            NonWorkingTimeLengthUserTop nonWorkingTimeLengthTop;
            Map<String, Object> configParamMap = baseDAO.getConfigParam();
            double MINUTE_BY_ONE_CLICK = Double.parseDouble(String.valueOf(configParamMap.get("MINUTE_BY_ONE_CLICK")));
            for (Map.Entry<String, Long> mapping : list) {
                nonWorkingTimeLengthTop = new NonWorkingTimeLengthUserTop();
                String employeenName = mapping.getKey();
                nonWorkingTimeLengthTop.setName(employeenName);
                nonWorkingTimeLengthTop.setTimes(FormulaUtil.timesToHourMinute(mapping.getValue(),String.valueOf(MINUTE_BY_ONE_CLICK)));
                nonWorkingTimeLengthTop.setNumber(employeeBasicMap.get(employeenName).get(0));
                nonWorkingTimeLengthTop.setDepartment(employeeBasicMap.get(employeenName).get(1));
                Map<String, Long> appFrequency = employeeAppFrequencyMap.get(employeenName);
                List<Map.Entry<String, Long>> list2 = new ArrayList<Map.Entry<String, Long>>(appFrequency.entrySet());
                // 通过比较器来实现排序
                list2.sort(new Comparator<Map.Entry<String, Long>>() {
                    @Override
                    public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                        // 降序排序
                        return o2.getValue().compareTo(o1.getValue());
                    }
                });
                AppTime appTime = null;
                List<AppTime> appTimeList = new ArrayList<>();
                for (Map.Entry<String, Long> mapping2 : list2) {
                    appTime = new AppTime();
                    appTime.setAppName(mapping2.getKey());
                    appTime.setTime(FormulaUtil.timesToHourMinute(mapping2.getValue(),String.valueOf(MINUTE_BY_ONE_CLICK)));
                    appTimeList.add(appTime);
                }
                nonWorkingTimeLengthTop.setAppTimeList(appTimeList);
                result.add(nonWorkingTimeLengthTop);
            }
        } catch (Exception e) {
            throw new UnifiedException(e);
        }
        return result;
    }
}