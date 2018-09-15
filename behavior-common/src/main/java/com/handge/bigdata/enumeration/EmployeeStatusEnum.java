package com.handge.bigdata.enumeration;

/**
 * 员工状态枚举
 *
 * @param
 * @author XiangChao
 * @date 2018/5/9 10:17
 * @return
 **/
public enum EmployeeStatusEnum {

    实习("1"),
    试用("2"),
    正式("3"),
    离职("4");

    private String code;

    EmployeeStatusEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
