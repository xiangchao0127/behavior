package com.handge.bigdata.resource.models.response.monitor;

/**
 * create by XC in 2018/07/12
 */
public class NetStatusHistory {
    /**
     * 时间
     */
    private String time;
    /**
     * ip
     */
    private String ip;
    /**
     * 标签
     */
    private String appClass;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAppClass() {
        return appClass;
    }

    public void setAppClass(String appClass) {
        this.appClass = appClass;
    }

    @Override
    public String toString() {
        return "NetStatusHistory{" +
                "time='" + time + '\'' +
                ", ip='" + ip + '\'' +
                ", appClass='" + appClass + '\'' +
                '}';
    }
}
