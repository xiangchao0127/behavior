package com.handge.bigdata.enumeration;

/**
 * Created by DaLu Guo on 2018/5/10.
 */
public enum TimeModelEnum {
    实时("2"),
    非实时("1");
    private String model;

    private TimeModelEnum(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }
}
