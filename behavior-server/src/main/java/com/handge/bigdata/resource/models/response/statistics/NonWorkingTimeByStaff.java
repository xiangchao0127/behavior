package com.handge.bigdata.resource.models.response.statistics;

import com.handge.bigdata.annotation.Hiding;

/**
 * 工作无关公司上网人均时长TOP
 *
 * @param
 * @author MaJianfu
 * @date 2018/4/25 10:20
 * @return
 **/
public class NonWorkingTimeByStaff {
    /**
     * 姓名
     */
    private String name;
    /**
     * 工号
     */
    private String userId;
    /**
     * 部门
     */
    private String department;
    /**
     * 时长
     */
    private String timeLength;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(String timeLength) {
        this.timeLength = timeLength;
    }

    public String getName() {

        return name;
    }

    @Hiding
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "NonWorkingTimeByStaff{" +
                "name='" + name + '\'' +
                ", userId='" + userId + '\'' +
                ", department='" + department + '\'' +
                ", timeLength=" + timeLength +
                '}';
    }
}
