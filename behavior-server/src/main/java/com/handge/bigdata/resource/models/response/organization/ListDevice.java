package com.handge.bigdata.resource.models.response.organization;

/**
 * @author liuqian
 * @date 2018/6/21
 * @Description:
 */
public class ListDevice {

    /**
     * 设备id
     */
    private String id;

    /**
     * 设备编号
     */
    private String deviceNumber;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备类型
     */
    private String type;

    /**
     * 员工姓名
     */
    private String employeeName;

    /**
     * 工号
     */
    private String employeeNumber;

    /**
     * Mac地址
     */
    private String Mac;

    /**
     * ip地址
     */
    private String ip;

    /**
     * 是否禁用
     */
    private String status;

    /**
     * 设备所属
     */
    private String property;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceNumber() {
        return deviceNumber;
    }

    public void setDeviceNumber(String deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getMac() {
        return Mac;
    }

    public void setMac(String mac) {
        Mac = mac;
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

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    @Override
    public String toString() {
        return "ListDevice{" +
                "id='" + id + '\'' +
                ", deviceNumber='" + deviceNumber + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", type='" + type + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", employeeNumber='" + employeeNumber + '\'' +
                ", Mac='" + Mac + '\'' +
                ", ip='" + ip + '\'' +
                ", status='" + status + '\'' +
                ", property='" + property + '\'' +
                '}';
    }
}
