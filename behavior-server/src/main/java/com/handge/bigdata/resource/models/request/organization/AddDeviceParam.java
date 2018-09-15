package com.handge.bigdata.resource.models.request.organization;

/**
 * @author liuqian
 * @date 2018/6/21
 * @Description:
 */
public class AddDeviceParam {

    /**
     * 设备名称
     */
    private String deviceName;

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
     * 设备所属
     */
    private String property;

    /**
     * Mac地址
     */
    private String mac;

    /**
     * ip地址
     */
    private String ip;

    /**
     * 是否禁用
     */
    private String status;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

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

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AddDeviceParam{" +
                "deviceName='" + deviceName + '\'' +
                ", accountId='" + accountId + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", employeeNumber='" + employeeNumber + '\'' +
                ", property='" + property + '\'' +
                ", mac='" + mac + '\'' +
                ", ip='" + ip + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
