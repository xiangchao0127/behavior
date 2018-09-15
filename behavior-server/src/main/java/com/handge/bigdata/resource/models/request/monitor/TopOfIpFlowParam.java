package com.handge.bigdata.resource.models.request.monitor;

import com.handge.bigdata.resource.models.request.base.UserContextParam;

import javax.validation.constraints.Digits;
import java.io.Serializable;

/**
 * Created by DaLu Guo on 2018/6/1.
 */
public class TopOfIpFlowParam extends UserContextParam implements Serializable {

    /**
     * 排名数量
     */
    @Digits(integer = 2, fraction = 20,message="排名数量")
    private int n = 5;

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    @Override
    public String toString() {
        return "TopOfIpFlowParam{" +
                "n=" + n +
                '}';
    }
}
