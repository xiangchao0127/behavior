package com.handge.bigdata.resource.service.api.monitor;


import com.google.inject.ImplementedBy;
import com.handge.bigdata.resource.models.request.monitor.AlarmInfoDetailParam;
import com.handge.bigdata.resource.models.request.monitor.AlarmInfoParam;
import com.handge.bigdata.resource.service.impl.monitor.AbnormalWathcherImpl;


@ImplementedBy(AbnormalWathcherImpl.class)

/**
 * @author guodalu
 * @date 2018/4/25
 */
public interface IAbnormalWathcher {

    /**
     * 单事件异常报警
     *
     * @return
     */
    public Object listSingleAlarmInfo(AlarmInfoParam alarmInfoParam);

    /**
     * 单事件异常报警详情
     *
     * @return
     */
    public Object listSingleAlarmInfoDetail(AlarmInfoDetailParam alarmInfoDetailParam);

    /**
     * 单事件异常报警
     *
     * @return
     */
    public Object listMultiAlarmInfo(AlarmInfoParam alarmInfoParam);

    /**
     * 单事件异常报警详情
     *
     * @return
     */
    public Object listMultiAlarmInfoDetail(AlarmInfoDetailParam alarmInfoDetailParam);


    /**
     * 异常报警,查询mysql
     *
     * @return
     */
    public Object listAlarmInfoNew(AlarmInfoParam alarmInfoParam);

    /**
     * 异常报警详情,查询mysql
     *
     * @return
     */
    public Object listAlarmInfoDetailNew(AlarmInfoDetailParam alarmInfoDetailParam);

}
