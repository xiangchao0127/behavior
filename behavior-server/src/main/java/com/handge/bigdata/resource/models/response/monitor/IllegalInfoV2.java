package com.handge.bigdata.resource.models.response.monitor;

/**
 * @author liuqian
 * @date 2018/4/25
 */
public class IllegalInfoV2 {
    /**
     * 姓名
     */
    private String name;
    /**
     * 工号
     */
    private String number;
    /**
     * 部门
     */
    private String department;
    /**
     * 应用名
     */
    private String appName;

    /**
     * 访问时间
     */
    private String accessTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserNumber() {
        return number;
    }

    public void setUserNumber(String userNumber) {
        this.number = userNumber;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(String accessTime) {
        this.accessTime = accessTime;
    }

    @Override
    public String toString() {
        return "IllegalInfo{" +
                "name='" + name + '\'' +
                ", userNumber='" + number + '\'' +
                ", department='" + department + '\'' +
                ", appName='" + appName + '\'' +
                ", accessTime='" + accessTime + '\'' +
                '}';
    }
}
