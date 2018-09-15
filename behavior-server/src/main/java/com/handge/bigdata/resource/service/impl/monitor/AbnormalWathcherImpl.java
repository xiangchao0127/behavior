/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.handge.bigdata.resource.service.impl.monitor;

import com.handge.bigdata.UnifiedException;
import com.handge.bigdata.dao.ProxyFactory;
import com.handge.bigdata.dao.SQLBuilder;
import com.handge.bigdata.dao.api.IBaseDAO;
import com.handge.bigdata.dao.proxy.EsProxy;
import com.handge.bigdata.dao.proxy.MySQLProxy;
import com.handge.bigdata.dao.proxy.Proxy;
import com.handge.bigdata.enumeration.DAOProxyEnum;
import com.handge.bigdata.enumeration.DateFormatEnum;
import com.handge.bigdata.enumeration.ESIndexEnum;
import com.handge.bigdata.enumeration.ExceptionWrapperEnum;
import com.handge.bigdata.resource.models.request.monitor.AlarmInfoDetailParam;
import com.handge.bigdata.resource.models.request.monitor.AlarmInfoParam;
import com.handge.bigdata.resource.models.response.monitor.AbnormalAlarmInfo;
import com.handge.bigdata.resource.models.response.monitor.Alarm;
import com.handge.bigdata.resource.service.api.monitor.IAbnormalWathcher;
import com.handge.bigdata.utils.CollectionUtils;
import com.handge.bigdata.utils.DateUtil;
import com.handge.bigdata.utils.PageResults;
import com.handge.bigdata.utils.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scala.reflect.internal.Trees;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;

/**
 * @author liuqian
 * @date 2018/4/25
 */
@Component
public class AbnormalWathcherImpl implements IAbnormalWathcher {
    @Autowired
    IBaseDAO baseDAO;

    @Override
    public Object listMultiAlarmInfo(AlarmInfoParam alarmInfoParam) {
        //
        return null;
    }

    @Override
    public Object listMultiAlarmInfoDetail(AlarmInfoDetailParam alarmInfoDetailParam) {
        return null;
    }

    /**
     * 单事件查询mysql
     * @param alarmInfoParam
     * @return
     */
    @Override
    public Object listSingleAlarmInfo(AlarmInfoParam alarmInfoParam) {
        //部门名称
        String departmentName = alarmInfoParam.getDepartment();
        // TODO: 2018/7/13 时间确定
        String startTime = null;
        try {
            startTime = DateUtil.timeStampToStrDate(System.currentTimeMillis(), DateFormatEnum.DAY);
        } catch (Exception e) {
            throw new UnifiedException(e);
        }
        String endTime = null;
        try {
            endTime = DateUtil.getNextDate(startTime,0,1);
        } catch (ParseException e) {
            throw new UnifiedException(e);
        }
        //获取报警信息
        ArrayList<Alarm> alarmInfo = null;
        ArrayList<String> ips = new ArrayList<>();
        if(StringUtils.notEmpty(departmentName)) {
           ips = baseDAO.getEmployeeIps(alarmInfoParam.getDepartment()).get(alarmInfoParam.getDepartment());
        }
        alarmInfo = getAlarmInfoByIds(startTime, endTime,ips);

        //封装数据到前段
        List<AbnormalAlarmInfo> result = wrapData(alarmInfo);
        return result.subList(0,result.size() > alarmInfoParam.getN() ? alarmInfoParam.getN():result.size());
    }

    /**
     * 单事件查询mysql
     * @param alarmInfoDetailParam
     * @return
     */
    @Override
    public Object listSingleAlarmInfoDetail(AlarmInfoDetailParam alarmInfoDetailParam) {
        if(StringUtils.isEmpty(alarmInfoDetailParam.getStartTime()) || StringUtils.isEmpty(alarmInfoDetailParam.getEndTime())){
            throw new UnifiedException("查询时间", ExceptionWrapperEnum.IllegalArgumentException);
        }
        //查询ips
        ArrayList<String> ips =(ArrayList) getIps(alarmInfoDetailParam.getDepartment(), alarmInfoDetailParam.getName(), alarmInfoDetailParam.getNumber());
        ArrayList<Alarm> alarms = getAlarmInfoByIds(alarmInfoDetailParam.getStartTime(), alarmInfoDetailParam.getEndTime(), ips);
        //封装数据到前端
        List<AbnormalAlarmInfo> result = wrapData(alarms);
        PageResults<AbnormalAlarmInfo> pageResult = CollectionUtils.getPageResult(result,alarmInfoDetailParam.getPageNo(),alarmInfoDetailParam.getPageSize());
        return pageResult;
    }

    public ArrayList<Alarm> getAlarmInfoByIds(String startTime, String endTime, List<String> ips) {
        MySQLProxy mySQLProxy = ProxyFactory.createProxy(DAOProxyEnum.MySQL);
        String preSql = "SELECT localIP,appName,accessTime,appClass " +
                "FROM app_abnormal_info " +
                "WHERE accessTime >= #{startTime} " +
                "AND accessTime <= #{endTime} " +
                "AND attr = '0'  " ;
        if(ips.size() > 0){
            preSql +=  "AND localIP in #{ips} " ;
        }
        preSql +=  "ORDER BY accessTime DESC";
        SQLBuilder builder =  SQLBuilder.sql(preSql);
        if(ips.size() > 0){
            builder.setParamter("ips",ips);
        }
        String sql = builder
                .setParamter("startTime", startTime)
                .setParamter("endTime",endTime)
                .toString();
        ResultSet resultSet = mySQLProxy.queryBySQL(sql);
        ArrayList<Alarm> alarmList = new ArrayList<>();
        try {
            while (resultSet.next()) {
                Alarm alarm = new Alarm();
                alarm.setIp(resultSet.getString("localIP"));
                alarm.setAppName(resultSet.getString("appName"));
                alarm.setAccessTime(resultSet.getDate("accessTime"));
                alarm.setAppClass(resultSet.getString("appClass"));
                alarmList.add(alarm);
            }
        } catch (SQLException e) {
            throw new UnifiedException(e);
        }
        return alarmList;
    }

    private List<AbnormalAlarmInfo> wrapData(ArrayList<Alarm> alarms ){
        HashMap<String, String> allEmployeeIpAndNumber = baseDAO.getAllEmployeeIpAndNumber();
        Map<String, String> allEmployeeNumberAndName = baseDAO.getAllEmployeeNumberAndName();
        ArrayList<AbnormalAlarmInfo> arrayList = new ArrayList<>();
        Set<String> allEmployeeIp = allEmployeeIpAndNumber.keySet();
        for(Alarm alarm : alarms){
            if(allEmployeeIp.contains(alarm.getIp())){
                AbnormalAlarmInfo abnormalAlarmInfo = new AbnormalAlarmInfo();
                String number = allEmployeeIpAndNumber.get(alarm.getIp());
                abnormalAlarmInfo.setName(allEmployeeNumberAndName.get(number));
                abnormalAlarmInfo.setNumber(number);
                abnormalAlarmInfo.setDepartment(baseDAO.getDepInfoByNo(number)[0]);
                abnormalAlarmInfo.setContent(alarm.getAppClass());
                abnormalAlarmInfo.setTime(DateUtil.date2Str(alarm.getAccessTime(),DateFormatEnum.SECONDS));
                abnormalAlarmInfo.setDesc("有***的傾向");
                arrayList.add(abnormalAlarmInfo);
            }
        }
        return arrayList;
    }

    private List<String> getIps(String department,String name,String number){
        Proxy proxy = ProxyFactory.createProxy(DAOProxyEnum.MySQL);
        List<String> ips = new ArrayList<>();
        try {
            String sql = "\n" +
                    "SELECT\n" +
                    "\tdev.static_ip AS ip\n" +
                    "FROM\n" +
                    "\tentity_employee_information_basic emp\n" +
                    "JOIN auth_account cou ON cou.employee_id = emp.id\n" +
                    "JOIN entity_device_basic dev ON dev.account_id = cou.id\n" +
                    "INNER JOIN entity_department_information_basic AS dep ON emp.department_id = dep.department_id\n" +
                    "AND emp. STATUS != 4\n" ;
            if(StringUtils.notEmpty(department)){
                sql +=  "AND dep.department_name LIKE #{department}\n" ;
            }
            if(StringUtils.notEmpty(name)){
                sql +=   "AND emp.`name` LIKE #{name}\n" ;
            }
            if(StringUtils.notEmpty(number)){
                sql +=   "AND emp.number = #{number};";
            }
            SQLBuilder excuteSqlBuilder = SQLBuilder.sql(sql);

            if(StringUtils.notEmpty(department)){
                excuteSqlBuilder.setParamter("department","%"+ department + "%");
            }
            if(StringUtils.notEmpty(name)){
                excuteSqlBuilder.setParamter("name","%"+ name + "%");
            }
            if(StringUtils.notEmpty(number)){
                excuteSqlBuilder.setParamter("number",number);
            }
            String excuteSql = excuteSqlBuilder.toString();
            ResultSet tagResult = (ResultSet) proxy.queryBySQL(excuteSql);

            while (tagResult.next()) {
                ips.add(tagResult.getString(1));
            }
        }catch (Exception e){
            throw new UnifiedException(e);
        }
        return ips;
    }

    //查询ES
    @Override
    public Object listAlarmInfoNew(AlarmInfoParam alarmInfoParam) {
        Map<String, Object> configMap = baseDAO.getConfigParam();
        List<String> AlarmTags = Arrays.asList(configMap.get("ALARM_TAGS").toString().split(","));
        //region 1 查詢es
        EsProxy esProxy = ProxyFactory.createProxy(DAOProxyEnum.ES);
        TransportClient esClient = esProxy.getClient();
        String today = DateUtil.timeStampToStrDate(System.currentTimeMillis(), DateFormatEnum.DAY);
        String[] indices = esProxy.generateIndices(today, today, ESIndexEnum.MAPPING);
        String esType = configMap.get("ES_TYPE").toString();
        long startTimeStamp = 0;
        try {
            startTimeStamp = DateUtil.dateToStartOrEndStamp(DateUtil.timeStampToStrDate(System.currentTimeMillis(), DateFormatEnum.SECONDS), 0);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long endTimeStamp = System.currentTimeMillis();
        List<String> ips = new ArrayList<>();
        if(StringUtils.notEmpty(alarmInfoParam.getDepartment())){
            ips = baseDAO.getEmployeeIps(alarmInfoParam.getDepartment()).get(alarmInfoParam.getDepartment());
        }else {
            for (String key : baseDAO.getEmployeeIps("").keySet()) {
                ips.addAll(baseDAO.getEmployeeIps("").get(key));
            }
        }
        if(ips == null){
            return new AbnormalAlarmInfo();
        }
        SearchRequestBuilder builder = esClient.prepareSearch(indices)
                .setTypes(esType)
                .setQuery(QueryBuilders.boolQuery()
                        .filter(QueryBuilders.rangeQuery("startTime").from(startTimeStamp, true).to(endTimeStamp, true))
                        .filter(QueryBuilders.termsQuery("appTags", AlarmTags))
                        .filter(QueryBuilders.termsQuery("localIp", ips))
                ).addSort("startTime", SortOrder.DESC)
                ;
        SearchResponse response = esProxy.action(builder);
        esProxy.returnClient();
        //endregion
        List<AbnormalAlarmInfo> result =  wrapDataNew(response);

        return result.subList(0,alarmInfoParam.getN());
    }
    //查询ES
    @Override
    public Object listAlarmInfoDetailNew(AlarmInfoDetailParam alarmInfoDetailParam) {

        Map<String, Object> configMap = baseDAO.getConfigParam();
        List<String> AlarmTags = Arrays.asList(configMap.get("ALARM_TAGS").toString().split(","));
        //region 1 查詢es
        EsProxy esProxy = ProxyFactory.createProxy(DAOProxyEnum.ES);
        TransportClient esClient = esProxy.getClient();
        String esType = configMap.get("ES_TYPE").toString();
        //如果传入时间为空，则默认为最近1天
        if (StringUtils.isEmpty(alarmInfoDetailParam.getStartTime())) {
            alarmInfoDetailParam.setStartTime(DateUtil.getDateTimeSeveralDaysAgo(1));
        }
        if (StringUtils.isEmpty(alarmInfoDetailParam.getEndTime())) {
            alarmInfoDetailParam.setEndTime(DateUtil.date2Str(new Date(), DateFormatEnum.SECONDS));
        }
        long startTimeStamp;
        try {
            startTimeStamp = DateUtil.dateToTimeStamp(alarmInfoDetailParam.getStartTime(), DateFormatEnum.SECONDS);
        } catch (ParseException e) {
            throw new UnifiedException(e);
        }
        long endTimeStamp;
        try {
            endTimeStamp = DateUtil.dateToTimeStamp(alarmInfoDetailParam.getEndTime(), DateFormatEnum.SECONDS);
        } catch (ParseException e) {
            throw new UnifiedException(e);
        }
        String[] indices = esProxy.generateIndices(startTimeStamp, endTimeStamp, ESIndexEnum.MAPPING);
        List<String> ips = getIps(alarmInfoDetailParam.getDepartment(),alarmInfoDetailParam.getName(),alarmInfoDetailParam.getNumber());
        int startNum = (alarmInfoDetailParam.getPageNo() - 1) * alarmInfoDetailParam.getPageSize();
        int endSize = alarmInfoDetailParam.getPageNo() * alarmInfoDetailParam.getPageSize() <= 10000 ? alarmInfoDetailParam.getPageSize() : 10000 - startNum;
        SearchRequestBuilder builder = esClient.prepareSearch(indices)
                .setTypes(esType)
                .setQuery(QueryBuilders.boolQuery()
                        .filter(QueryBuilders.rangeQuery("startTime").from(startTimeStamp, true).to(endTimeStamp, true))
                        .filter(QueryBuilders.termsQuery("appTags", AlarmTags))
                        .filter(QueryBuilders.termsQuery("localIp", ips))
                ).addSort("startTime", SortOrder.DESC).setFrom(startNum).setSize(endSize);
        SearchResponse response = esProxy.action(builder);
        esProxy.returnClient();
        //endregion
        SearchHits hits = response.getHits();
        List<AbnormalAlarmInfo> result =  wrapDataNew(response);
        PageResults<AbnormalAlarmInfo> pageResult = new PageResults<>();
        pageResult.setCurrentPage(alarmInfoDetailParam.getPageNo());
        pageResult.setPageSize(alarmInfoDetailParam.getPageSize());
        pageResult.setTotalCount((int) hits.getTotalHits() > 10000 ? 10000 : (int) hits.getTotalHits());
        pageResult.setPageCount(((pageResult.getTotalCount() - 1) / alarmInfoDetailParam.getPageSize()) + 1);
        pageResult.setNextPageNo(alarmInfoDetailParam.getPageNo() + 1 < pageResult.getPageCount() ? alarmInfoDetailParam.getPageNo() + 1 : pageResult.getPageCount());
        pageResult.setResults(result);
        return pageResult;
    }

    private List<AbnormalAlarmInfo> wrapDataNew(SearchResponse response){
        //region封裝數據
        List<AbnormalAlarmInfo> result = new ArrayList<>();
        SearchHits hits = response.getHits();
        SearchHit[] searchHits = hits.hits();
        HashMap<String, String> allEmployeeIpAndNumber = baseDAO.getAllEmployeeIpAndNumber();
        Map<String, List<String>> allDepartmentAndNumbers = getDepartmentAndNumbers();
        Map<String, String> allEmployeeNumberAndName = baseDAO.getAllEmployeeNumberAndName();
        for (SearchHit searchHit : searchHits) {
            String ip = searchHit.getSource().get("localIp").toString();
            if(allEmployeeIpAndNumber.keySet().contains(ip)){
                long strTime = Long.parseLong(searchHit.getSource().get("startTime").toString());
                String content = searchHit.getSource().get("appTags").toString();
                String number = allEmployeeIpAndNumber.get(ip);
                AbnormalAlarmInfo abnormalAlarmInfo = new AbnormalAlarmInfo();
                abnormalAlarmInfo.setTime(DateUtil.timeStampToStrDate(strTime,DateFormatEnum.SECONDS));
                abnormalAlarmInfo.setNumber(number);
                abnormalAlarmInfo.setContent(content);
                abnormalAlarmInfo.setDesc("有***的傾向");
                abnormalAlarmInfo.setName(allEmployeeNumberAndName.get(number));
                for(String dept : allDepartmentAndNumbers.keySet()){
                    if(allDepartmentAndNumbers.get(dept).contains(number)){
                        abnormalAlarmInfo.setDepartment(dept);
                        break;
                    }
                }
                result.add(abnormalAlarmInfo);
            }
        }
        return result;
    }

    /** NEW使用
     * s获取部门和工号的集合
     * @return
     */
    private Map<String,List<String>> getDepartmentAndNumbers(){
        Proxy proxy = ProxyFactory.createProxy(DAOProxyEnum.MySQL);
        HashMap<String, List<String>> allDepartmentAndNumbers = new HashMap<>();
        try {
            String sql = "SELECT\n" +
                    "\td.department_name AS dept,\n" +
                    "e.number\n" +
                    "FROM\n" +
                    "\tentity_employee_information_basic e\n" +
                    "INNER JOIN entity_department_information_basic d ON e.department_id = d.department_id\n" +
                    "WHERE\n" +
                    "e. STATUS != 4;";
            String excuteSql = SQLBuilder.sql(sql).toString();
            ResultSet tagResult = (ResultSet) proxy.queryBySQL(excuteSql);

            while (tagResult.next()) {
                String dept = tagResult.getString(1);
                if(allDepartmentAndNumbers.keySet().contains(dept)){
                    allDepartmentAndNumbers.get(dept).add(tagResult.getString(2));
                }else{
                    List<String> numList = new ArrayList<>();
                    numList.add(tagResult.getString(2));
                    allDepartmentAndNumbers.put(dept,numList);
                }
            }
        }catch (Exception e){
            throw new UnifiedException(e);
        }
        return allDepartmentAndNumbers;
    }
}
