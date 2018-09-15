package com.handge.bigdata.resource.models.response.monitor;

import com.handge.bigdata.annotation.Hiding;

import java.util.List;

/**
 * 非工作性上网时长
 *
 * @author liuqian
 * @date 2018/4/25
 */
public class NonWorkingTimeLengthUserTop {
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
     * 时长
     */
    private String times;

    /**
     * 应用耗时
     */
    private List<AppTime> appTimeList;

    public String getName() {
        return name;
    }

    @Hiding
    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    @Override
    public String toString() {
        return "NonWorkingTimeLengthUserTop{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", department='" + department + '\'' +
                ", times='" + times + '\'' +
                ", appTimeList=" + appTimeList +
                '}';
    }

    public List<AppTime> getAppTimeList() {
        return appTimeList;
    }

    public void setAppTimeList(List<AppTime> appTimeList) {
        this.appTimeList = appTimeList;
    }

}
