package com.handge.bigdata.resource.models.request.professional;

import com.handge.bigdata.resource.models.request.base.TimeParam;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * Created by DaLu Guo on 2018/6/12.
 */
public class ProfessionalAccomplishmentParam extends TimeParam implements Serializable {

    /**
     * 公司/部门/个人
     */
    @NotEmpty(message = "模式")
    @Min(value = 1,message = "模式")
    @Max(value = 3,message = "模式")
    private String model;

    /**
     * 工号
     */
    private String number;

    /**
     * 部门
     */
    private String department;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "ProfessionalAccomplishmentParam{" +
                "model='" + model + '\'' +
                ", number='" + number + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
