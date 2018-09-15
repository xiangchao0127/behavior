package com.handge.bigdata.resource.models.response.monitor;

/**
 * @author liuqian
 * @date 2018/7/16
 * @Description: 非工作性上网应用时长
 */
public class NonWorkingAppTimeTop {
    /**
     * 应用名称
     */
    private String appName;

    /**
     * 时长
     */
    private String times;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    @Override
    public String toString() {
        return "NonWorkingAppTimeTop{" +
                "appName='" + appName + '\'' +
                ", times='" + times + '\'' +
                '}';
    }
}
