package com.handge.bigdata.resource.models.request.statistics;

import com.handge.bigdata.enumeration.TimeSectionEnum;
import com.handge.bigdata.resource.models.request.base.TimeParam;

import java.io.Serializable;

/**
 * Created by DaLu Guo on 2018/6/1.
 */
public class NonWorkingTimeSoltParam extends TimeParam implements Serializable {
    /**
     * 部门名称
     */
    private String department;
    /**
     * 模式  周(W),天(D),小时(H)
     */
    private TimeSectionEnum section;

    private String jobProperty;

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

    public String getJobProperty() {
        return jobProperty;
    }

    public void setJobProperty(String jobProperty) {
        this.jobProperty = jobProperty;
    }

    @Override
    public String toString() {
        return "NonWorkingTimeSoltParam{" +
                "department='" + department + '\'' +
                ", section=" + section +
                ", jobProperty='" + jobProperty + '\'' +
                '}';
    }
}
