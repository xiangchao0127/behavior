package com.handge.bigdata.resource.models.response.statistics;

public class NonWorkingSoft {
    private String time;
    private String ip;
    private String app_Class;
    private String count;

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

    public String getApp_Class() {
        return app_Class;
    }

    public void setApp_Class(String app_Class) {
        this.app_Class = app_Class;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "NonWorkingTrendTemp{" +
                "time='" + time + '\'' +
                ", ip='" + ip + '\'' +
                ", app_Class='" + app_Class + '\'' +
                ", count='" + count + '\'' +
                '}';
    }
}
