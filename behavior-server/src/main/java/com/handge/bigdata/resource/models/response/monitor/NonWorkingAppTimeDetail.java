package com.handge.bigdata.resource.models.response.monitor;

/**
 * @author liuqian
 * @date 2018/7/17
 * @Description:
 */
public class NonWorkingAppTimeDetail {
    /**
     * 应用名称
     */
    private String appName;

    /**
     * 时长
     */
    private String times;

    /**
     * 标签
     */
    private String basicTag;

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

    public String getBasicTag() {
        return basicTag;
    }

    public void setBasicTag(String basicTag) {
        this.basicTag = basicTag;
    }

    @Override
    public String toString() {
        return "NonWorkingAppTimeDetail{" +
                "appName='" + appName + '\'' +
                ", times='" + times + '\'' +
                ", basicTag='" + basicTag + '\'' +
                '}';
    }
}
