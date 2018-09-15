package com.handge.bigdata.resource.models.request.common;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * Created by DaLu Guo on 2018/6/5.
 */
public class IpsByNumberParam implements Serializable {
    /**
     * 员工编号
     */
    @NotEmpty(message = "员工编号")
    private String number;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "IpsByNumberParam{" +
                "number='" + number + '\'' +
                '}';
    }
}
