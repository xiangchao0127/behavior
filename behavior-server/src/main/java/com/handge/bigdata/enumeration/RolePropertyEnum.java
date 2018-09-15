package com.handge.bigdata.enumeration;

/**
 * @author Liujuhao
 * @date 2018/6/21.
 */
public enum RolePropertyEnum {

    Default("00","普通员工"),
    TopManager("01","公司领导"),
    DepartmentManager("02", "部门领导"),
    HR("03","HR"),
    Admin("04", "网管员"),
    HyperVisor("99", "超级管理员")
    ;

    private String code;

    private String name;

    RolePropertyEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
