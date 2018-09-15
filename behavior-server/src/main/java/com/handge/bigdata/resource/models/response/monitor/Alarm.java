package com.handge.bigdata.resource.models.response.monitor;

import java.util.Date;

/**
 * create by XC at 2017/7/9
 */
public class Alarm {
    /**
     * ip
     */
    private String ip;
    /**
     * 应用名称
     */
    private String appName;

    /**
     * 访问时间
     */
    private Date accessTime;

    /**
     * 应用类型
     */
    private String appClass;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Date getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(Date accessTime) {
        this.accessTime = accessTime;
    }

    public String getAppClass() {
        return appClass;
    }

    public void setAppClass(String appClass) {
        this.appClass = appClass;
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "ip='" + ip + '\'' +
                ", appName='" + appName + '\'' +
                ", accessTime=" + accessTime +
                ", appClass='" + appClass + '\'' +
                '}';
    }
}
