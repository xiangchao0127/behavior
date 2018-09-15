package com.handge.bigdata.resource.models.request.monitor;

import com.handge.bigdata.resource.models.request.base.UserContextParam;

import java.io.Serializable;

/**
 * Created by DaLu Guo on 2018/6/1.
 */
public class TopOfNonWorkingTimeParam extends UserContextParam implements Serializable {
    /**
     * 排名数量（1,2,3,4,5...），如果不传则默认为拿top 5
     */
    private int n = 5;

    /**
     * 部门名称
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
        return "TopOfNonWorkingTimeParam{" +
                "n=" + n +
                ", department='" + department + '\'' +
                '}';
    }
}
