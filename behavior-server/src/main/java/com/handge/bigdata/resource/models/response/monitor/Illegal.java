package com.handge.bigdata.resource.models.response.monitor;

import java.util.Date;

public class Illegal {

    private String ip;
    /**
     * 应用名称
     */
    private String appName;

    /**
     * 访问时间
     */
    private Date accessTime;

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

    @Override
    public String toString() {
        return "Illegal{" +
                "ip='" + ip + '\'' +
                ", appName='" + appName + '\'' +
                ", accessTime='" + accessTime + '\'' +
                '}';
    }
}
