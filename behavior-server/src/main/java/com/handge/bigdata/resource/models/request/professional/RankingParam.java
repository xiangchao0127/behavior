package com.handge.bigdata.resource.models.request.professional;

import com.handge.bigdata.resource.models.request.base.UserContextParam;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 行为素养员工排名请求参数
 * create by xc on 2018/6/13
 */
public class RankingParam extends UserContextParam implements Serializable {
    /**
     * 员工工号
     */
    @NotEmpty(message = "员工工号")
    private String number;
    /**
     * 时间
     */
    private String time;


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

    @Override
    public String toString() {
        return "RankingParam{" +
                "number='" + number + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
