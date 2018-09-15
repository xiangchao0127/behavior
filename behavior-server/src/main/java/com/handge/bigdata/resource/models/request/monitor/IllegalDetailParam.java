package com.handge.bigdata.resource.models.request.monitor;

import com.handge.bigdata.resource.models.request.base.PageParam;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * Created by DaLu Guo on 2018/5/22.
 */
public class IllegalDetailParam extends PageParam implements Serializable {
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
    /**
     * 开始时间
     */
    @NotEmpty(message = "开始时间")
    private String startTime;
    /**
     * 结束时间
     */
    @NotEmpty(message = "结束时间")
    private String endTime;

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
        return "IllegalDetailParam{" +
                "number='" + number + '\'' +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
