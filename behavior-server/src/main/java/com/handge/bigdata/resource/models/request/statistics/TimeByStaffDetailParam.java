package com.handge.bigdata.resource.models.request.statistics;

import com.handge.bigdata.resource.models.request.base.TimeParam;

import java.io.Serializable;

/**
 * Created by DaLu Guo on 2018/6/1.
 */
public class TimeByStaffDetailParam extends TimeParam implements Serializable {
    /**
     * 部门
     */
    private String department;

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "TimeByStaffDetailParam{" +
                "department='" + department + '\'' +
                '}';
    }

}
