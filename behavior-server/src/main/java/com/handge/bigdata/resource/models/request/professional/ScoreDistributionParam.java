package com.handge.bigdata.resource.models.request.professional;

import com.handge.bigdata.resource.models.request.base.UserContextParam;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * create by xc on 2018/6/13
 */
public class ScoreDistributionParam extends UserContextParam implements Serializable {
    /**
     * 模式(公司,部门,个人)
     */
    @NotEmpty(message = "模式编号")
    private String model;
    /**
     * 工号
     */
    private String number;
    /**
     * 时间
     */
    private String time;
    /**
     * 部门名称
     */
    private String department;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "ScoreDistributionParam{" +
                "model='" + model + '\'' +
                ", number='" + number + '\'' +
                ", time='" + time + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
