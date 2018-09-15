package com.handge.bigdata.resource.models.response.monitor;

/**
 * Description:
 *
 * @author July
 * 2018/5/22 11:45
 */
public class AppTime {
    /**
     * 应用名称
     */
    private String appName;

    /**
     * 耗时
     */
    private String time;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "AppTime{" +
                "appName='" + appName + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
