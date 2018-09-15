package com.handge.bigdata.enumeration;

/**
 * Created by MaJianfu on 2018/6/13.
 */
public enum StaffModelEnum {
    优秀员工("1"),
    差劲员工("2");
    private String model;

    private StaffModelEnum(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }
}
