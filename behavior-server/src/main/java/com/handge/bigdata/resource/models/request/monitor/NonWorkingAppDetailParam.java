package com.handge.bigdata.resource.models.request.monitor;

import com.handge.bigdata.resource.models.request.base.PageParam;

import java.io.Serializable;

/**
 * Created by DaLu Guo on 2018/6/1.
 */
public class NonWorkingAppDetailParam extends PageParam implements Serializable {

    /**
     * 应用名称
     */
    private String appName;

    private String department;

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

    @Override
    public String toString() {
        return "NonWorkingAppDetailParam{" +
                "appName='" + appName + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
