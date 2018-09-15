package com.handge.bigdata.resource.models.response.statistics;

import com.handge.bigdata.annotation.Hiding;

import java.util.List;

/**
 * 上网足迹查询
 *
 * @param
 * @author MaJianfu
 * @date 2018/4/25 9:11
 * @return
 **/

public class InternetFootprint {
    /**
     * 用户
     */
    private String name;
    /**
     * 部门
     */
    private String department;
    /**
     * 工号
     */
    private String number;
    /**
     * 其他信息
     */
    private List<InternetFootprintInfo> infoList;

    public String getName() {
        return name;
    }

    @Hiding
    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public List<InternetFootprintInfo> getInfoList() {
        return infoList;
    }

    public void setInfoList(List<InternetFootprintInfo> infoList) {
        this.infoList = infoList;
    }

    @Override
    public String toString() {
        return "InternetFootprint{" +
                "name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", number='" + number + '\'' +
                ", infoList=" + infoList +
                '}';
    }
}