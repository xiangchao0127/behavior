package com.handge.bigdata.resource.models.request.organization;

import com.handge.bigdata.resource.models.request.base.PageParam;

import java.io.Serializable;

/**
 * @author liuqian
 * 2018/6/14 9:09
 */
public class ListEmployeeParam extends PageParam implements Serializable {
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
     * 角色
     */
    private String role;

    /**
     * 状态
     */
    private String status;

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ListEmployeeParam{" +
                "employeeName='" + employeeName + '\'' +
                ", employeeNumber='" + employeeNumber + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", role='" + role + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
