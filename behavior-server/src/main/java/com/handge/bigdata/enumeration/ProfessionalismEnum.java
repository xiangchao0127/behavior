package com.handge.bigdata.enumeration;

/**
 * Created by MaJianfu on 2018/6/14.
 */
public enum ProfessionalismEnum {
    loyalty("忠诚度"),
    working_attitude("工作态度"),
    compliance_discipline("遵纪守规程度");

    private String value;

    private ProfessionalismEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
