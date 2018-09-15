package com.handge.bigdata.resource.models.request.base;

import java.io.Serializable;

/**
 * Created by DaLu Guo on 2018/6/1.
 */
public class TimeParam extends PageParam implements Serializable {

    /**
     * 开始时间 2018-01-01 00:00:00
     */
    private String startTime;
    /**
     * 结束时间 2018-01-01 00:00:00
     */
    private String endTime;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "TimeParam{" +
                "startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
