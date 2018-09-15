package com.handge.bigdata.resource.models.request.professional;

import com.handge.bigdata.resource.models.request.base.UserContextParam;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 职业素养趋势请求参数
 * create by xc on 2018/6/12
 */
public class ProfessionalTrendParam extends UserContextParam implements Serializable {
    /**
     * 模式(公司,部门,个人)
     */
    @NotEmpty(message = "模式编号")
    private String model;
    /**
     * 时间
     */
    private String time;

    /**
     * 工号
     */
    private String number;

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

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "ProfessionalTrendParam{" +
                "model='" + model + '\'' +
                ", time='" + time + '\'' +
                ", number='" + number + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
