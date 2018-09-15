package com.handge.bigdata.resource.models.request.monitor;

import com.handge.bigdata.resource.models.request.base.PageParam;

import java.io.Serializable;

/**
 * Created by DaLu Guo on 2018/6/1.
 */
public class NonWorkingTimeDetailParam extends PageParam implements Serializable {
    /**
     * 工号
     */
    private String number;
    /**
     * 姓名
     */
    private String name;
    /**
     * 部门
     */
    private String department;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "NonWorkingTimeDetailParam{" +
                "number='" + number + '\'' +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
