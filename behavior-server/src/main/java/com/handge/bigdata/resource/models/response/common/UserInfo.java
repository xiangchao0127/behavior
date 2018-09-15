package com.handge.bigdata.resource.models.response.common;

import java.util.List;

/**
 * 用户信息
 * Created by DaLu Guo on 2018/5/29.
 */

public class UserInfo {

    /**
     * 员工姓名
     */
    private String employeeName;
    /**
     * 部门名称
     */
    private String department;
    /**
     * 员工编号
     */
    private String number;
    /**
     * 角色性质
     */
    private List<String> roleList;

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public List<String> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<String> roleList) {
        this.roleList = roleList;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "employeeName='" + employeeName + '\'' +
                ", department='" + department + '\'' +
                ", number='" + number + '\'' +
                ", roleList=" + roleList +
                '}';
    }
}
