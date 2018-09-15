package com.handge.bigdata.resource.models.request.professional;

import com.handge.bigdata.resource.models.request.base.TimeParam;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * Created by MaJianfu on 2018/6/12.
 */
public class TopOfStaffParam extends TimeParam implements Serializable {
    /**
     * 排名数量
     */
    @Digits(integer = 2, fraction = 20,message="排名数量")
    private int n = 5;
    /**
     * 优秀员工或者差劲员工
     */
    @Min(1)
    @Max(2)
    private String model;
    /**
     * 部门
     */
    @NotEmpty(message="部门")
    private String department;

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "TopOfStaffParam{" +
                "n=" + n +
                ", model='" + model + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
