package com.handge.bigdata.resource.models.response.organization;

import java.util.List;

/**
 * @author liuqian
 * @date 2018/6/20
 * @Description:
 */
public class ListEmployeeInfo {
    /**
     * 账号id
     */
    private String accountId;

    /**
     * 员工姓名
     */
    private String employeeName;

    /**
     * 工号
     */
    private String employeeNumber;

    /**
     * 部门
     */
    private String departmentName;

    /**
     * 职位
     */
    private String position;

    /**
     * 角色
     */
    private List<String> roles;

    /**
     * 状态
     */
    private String status;

    /**
     * 账号状态
     */
    private String accountStatus;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }


    @Override
    public String toString() {
        return "ListEmployeeInfo{" +
                "accountId='" + accountId + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", employeeNumber='" + employeeNumber + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", position='" + position + '\'' +
                ", roles=" + roles +
                ", status='" + status + '\'' +
                ", accountStatus='" + accountStatus + '\'' +
                '}';
    }
}
