package com.handge.bigdata.enumeration;

public enum JobPropertyEnum {

    工作相关("1","工作相关"),
    工作无关("0","工作无关"),
    不确定("2","不确定");

    private String code;
    private String desc;

    JobPropertyEnum(String code,String desc) {
        this.code = code;
        this.desc = desc;
    }



    public String getCode() {
        return code;
    }
    public String getDesc() {
        return desc;
    }
}
