package com.handge.bigdata.resource.models.response.statistics;

public class NonWorkingSoftNew {
    private String day;
    private String hour;
    private String ip;
    private String appClass;
    private Long count;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
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

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "NonWorkingTrendTempNew{" +
                "day='" + day + '\'' +
                ", hour='" + hour + '\'' +
                ", ip='" + ip + '\'' +
                ", appClass='" + appClass + '\'' +
                ", count='" + count + '\'' +
                '}';
    }
}
