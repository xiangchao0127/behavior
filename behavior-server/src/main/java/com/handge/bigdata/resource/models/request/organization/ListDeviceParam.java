package com.handge.bigdata.resource.models.request.organization;

import com.handge.bigdata.resource.models.request.base.PageParam;

import java.io.Serializable;

/**
 * @author liuqian
 * @date 2018/6/21
 * @Description:
 */
public class ListDeviceParam extends PageParam implements Serializable {

    /**
     * 员工姓名
     */
    private String employeeName;

    /**
     * 工号
     */
    private String number;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * ip地址
     */
    private String ip;

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "ListDeviceParam{" +
                "employeeName='" + employeeName + '\'' +
                ", number='" + number + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }
}
