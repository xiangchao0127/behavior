package com.handge.bigdata.resource.models.response.organization;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @author liuqian
 * @date 2018/6/20
 * @Description:
 */
public class SelectEmployee implements Serializable{

    /**
     * 账号id
     */
    private String accountId;

    /**
     * 员工姓名
     */
    @NotEmpty
    private String employeeName;

    /**
     * 工号
     */
    @NotEmpty
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
     * 工龄
     */
    private String seniority;

    /**
     * 岗龄
     */
    private String postAge;

    /**
     * 状态
     */
    private String status;

    /**
     * 账号状态
     */
    private String accountStatus;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 身份证号码
     */
    private String idCard;

    /**
     * 居住地址
     */
    private String homeAddress;

    private List<DeviceItemByEmployee> deviceByEmployeeList;

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

    public String getSeniority() {
        return seniority;
    }

    public void setSeniority(String seniority) {
        this.seniority = seniority;
    }

    public String getPostAge() {
        return postAge;
    }

    public void setPostAge(String postAge) {
        this.postAge = postAge;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public List<DeviceItemByEmployee> getDeviceByEmployeeList() {
        return deviceByEmployeeList;
    }

    public void setDeviceByEmployeeList(List<DeviceItemByEmployee> deviceByEmployeeList) {
        this.deviceByEmployeeList = deviceByEmployeeList;
    }

    @Override
    public String toString() {
        return "SelectEmployee{" +
                "accountId='" + accountId + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", employeeNumber='" + employeeNumber + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", position='" + position + '\'' +
                ", roles=" + roles +
                ", seniority='" + seniority + '\'' +
                ", postAge='" + postAge + '\'' +
                ", status='" + status + '\'' +
                ", accountStatus='" + accountStatus + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", idCard='" + idCard + '\'' +
                ", homeAddress='" + homeAddress + '\'' +
                ", deviceByEmployeeList=" + deviceByEmployeeList +
                '}';
    }
}
