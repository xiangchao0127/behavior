package com.handge.bigdata.enumeration;

/**
 * Created by MaJianfu on 2018/6/25.
 */
public enum GradeEnum {
    A("优"),
    B("良"),
    C("中"),
    D("差"),
    E("劣");

    private String value;

    private GradeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
