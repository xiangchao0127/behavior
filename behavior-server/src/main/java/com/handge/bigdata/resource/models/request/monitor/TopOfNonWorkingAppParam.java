package com.handge.bigdata.resource.models.request.monitor;

import com.handge.bigdata.resource.models.request.base.UserContextParam;

import javax.validation.constraints.Digits;
import java.io.Serializable;

/**
 *
 * @author DaLu Guo
 * @date 2018/6/1
 */
public class TopOfNonWorkingAppParam extends UserContextParam implements Serializable {

    /**
     * 排名数量（1,2,3,4,5...）
     */
    @Digits(integer = 2, fraction = 20,message = "排名数量")
    private int n = 5;

    private String department;

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    @Override
    public String toString() {
        return "TopOfNonWorkingAppParam{" +
                "n=" + n +
                ", department='" + department + '\'' +
                '}';
    }
}
