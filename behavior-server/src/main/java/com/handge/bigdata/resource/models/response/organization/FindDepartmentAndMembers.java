package com.handge.bigdata.resource.models.response.organization;

import java.util.Map;

/**
 * @author liuqian
 * @date 2018/6/25
 * @Description:
 */
public class FindDepartmentAndMembers {

    /**
     * 部门id
     */
    private String departmentId;

    /**
     * 部门名称
     */
    private String departmentName;

    /**
     * 员工id和姓名
     */
    private Map<String,String> employeeIdName;

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Map<String, String> getEmployeeIdName() {
        return employeeIdName;
    }

    public void setEmployeeIdName(Map<String, String> employeeIdName) {
        this.employeeIdName = employeeIdName;
    }

    @Override
    public String toString() {
        return "FindDepartmentAndMembers{" +
                "departmentId='" + departmentId + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", employeeIdName=" + employeeIdName +
                '}';
    }
}
