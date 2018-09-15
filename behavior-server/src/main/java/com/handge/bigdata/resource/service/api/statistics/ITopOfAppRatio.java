package com.handge.bigdata.resource.service.api.statistics;

import com.handge.bigdata.resource.models.UserContext;

/**
 * Created by DaLu Guo on 2018/5/4.
 */

public interface ITopOfAppRatio {
    /**
     * 应用占比Top (已弃用)
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param tagNum    标签排名的数量
     * @param appNum    应用排名的数量
     * @param context   用户信息
     * @param threshold 一个上工作无关网站的时长，等于或大于这个时长的才统计
     * @return
     */
    @Deprecated
    public Object listTopOfAppRatio(String startTime, String endTime, int tagNum, int appNum, UserContext context, double threshold);
}
