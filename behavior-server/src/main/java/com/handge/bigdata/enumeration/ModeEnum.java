package com.handge.bigdata.enumeration;

/**
 * create by xc on 2018/6/12
 */
public enum ModeEnum {
    PERSON("个人", "1"),
    DEPARTMENT("部门", "2"),
    COMAPNY("公司", "3");

    private String mode;
    private String desc;

    ModeEnum(String desc, String mode) {
        this.mode = mode;
        this.desc = desc;
    }

    public String getMode() {
        return mode;
    }

    public String getDesc() {
        return desc;
    }
}
