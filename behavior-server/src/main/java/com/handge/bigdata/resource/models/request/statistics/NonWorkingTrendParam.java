package com.handge.bigdata.resource.models.request.statistics;

import com.handge.bigdata.enumeration.TimeSectionEnum;
import com.handge.bigdata.resource.models.request.base.UserContextParam;

import java.io.Serializable;

/**
 * Created by DaLu Guo on 2018/6/1.
 */
public class NonWorkingTrendParam extends UserContextParam implements Serializable {
    /**
     * 时间，形如“2018-05-16 10:02:50”
     */
    private String date;
    /**
     * 阈值，每天上网时间（单位分钟）超过该阈值的才记入
     */
    private double threshold;
    /**
     * 部门（名称）
     */
    private String department;

    /**
     * 统计类型（按月、天）
     */
    private TimeSectionEnum section;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public TimeSectionEnum getSection() {
        return section;
    }

    public void setSection(TimeSectionEnum section) {
        this.section = section;
    }

    @Override
    public String toString() {
        return "NonWorkingTrendParam{" +
                "date='" + date + '\'' +
                ", threshold=" + threshold +
                ", department='" + department + '\'' +
                ", section=" + section +
                '}';
    }
}
