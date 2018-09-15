package com.handge.bigdata.resource.models.request.monitor;

import com.handge.bigdata.resource.models.request.base.UserContextParam;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * Created by DaLu Guo on 2018/6/1.
 */
public class FlowTendencyParam extends UserContextParam implements Serializable {

    /**
     * 两种模式：1=24小时模式 2=实时模式
     */
    @Min(value = 1,message = "模式")
    @Max(value = 2,message = "模式")
    private String model;
    /**
     * 应用数量,默认为3
     */
    @Digits(integer = 2, fraction = 0,message = "应用数量")
    private int size = 3;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "FlowTendencyParam{" +
                "model='" + model + '\'' +
                ", size=" + size +
                '}';
    }
}
