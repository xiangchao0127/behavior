package com.handge.bigdata.enumeration;

import com.handge.bigdata.UnifiedException;

/**
 * Created by DaLu Guo on 2018/6/13.
 */
public enum EmployeeStatusEnum {
    //    1：实习,2：试用,3：正式,4：离职
    PRACTICE("1", "实习"),
    PROBATION("2", "试用"),
    FORMAL("3", "正式"),
    QUIT("4", "离职");

    private String status;
    private String desc;

    EmployeeStatusEnum(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    /**
     * 根据状态查询对应的描述
     *
     * @param status
     * @return
     */
    public static String getDescByStatus(String status) {
        EmployeeStatusEnum[] values = EmployeeStatusEnum.values();
        for (EmployeeStatusEnum employeeStatusEnum : values) {
            if (employeeStatusEnum.getStatus().equals(status)) {
                return employeeStatusEnum.getDesc();
            }
        }
        throw new UnifiedException("状态", ExceptionWrapperEnum.Parameter_Enum_NOT_Match);
    }

    public static String getStatusByDesc(String desc) {
        EmployeeStatusEnum[] values = EmployeeStatusEnum.values();
        for (EmployeeStatusEnum employeeStatusEnum : values) {
            if (employeeStatusEnum.getDesc().equals(desc)) {
                return employeeStatusEnum.getStatus();
            }
        }
        throw new UnifiedException("状态", ExceptionWrapperEnum.Parameter_Enum_NOT_Match);
    }

    public String getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
