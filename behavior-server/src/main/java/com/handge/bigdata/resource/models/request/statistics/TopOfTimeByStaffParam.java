package com.handge.bigdata.resource.models.request.statistics;

import com.handge.bigdata.resource.models.request.base.TimeParam;

import java.io.Serializable;

/**
 * Created by DaLu Guo on 2018/6/1.
 */
public class TopOfTimeByStaffParam extends TimeParam implements Serializable {

    /**
     * 排名数量（1,2,3,4,5...）
     */
    private int n = 5;

    /**
     * 部门
     */
    private String department;

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "TopOfTimeByStaffParam{" +
                "n=" + n +
                ", department='" + department + '\'' +
                '}';
    }
}
