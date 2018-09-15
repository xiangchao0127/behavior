package com.handge.bigdata.resource.models.response.monitor;

import java.util.List;

/**
 * @author liuqian
 * @date 2018/4/25
 */
public class IllegalInfo {

    /**
     * 应用名
     */
    private String appName;

    /**
     * 次数
     */
    private String count;
    /**
     * 姓名集合
     */
    private List<String> name;

    public List<String> getName() {
        return name;
    }

    public void setName(List<String> name) {
        this.name = name;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "IllegalInfo{" +
                "name=" + name +
                ", appName='" + appName + '\'' +
                ", count='" + count + '\'' +
                '}';
    }
}
