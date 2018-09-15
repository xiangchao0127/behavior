package com.handge.bigdata.resource.models.response.organization;

/**
 * @author liuqian
 * @date 2018/6/22
 * @Description:
 */
public class FindNameAndNumber {
    /**
     * 账号id
     */
    private String accountId;

    /**
     * 部门
     */
    private String department;

    /**
     * 员工姓名
     */
    private String employeeName;

    /**
     * 工号
     */
    private String employeeNumber;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
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

    @Override
    public String toString() {
        return "FindNameAndNumber{" +
                "accountId='" + accountId + '\'' +
                ", department='" + department + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", employeeNumber='" + employeeNumber + '\'' +
                '}';
    }
}
