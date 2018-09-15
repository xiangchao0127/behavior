package com.handge.bigdata.resource.models.response.statistics;

/**
 * 工作无关部门上网人均时长TOP
 *
 * @param
 * @author MaJianfu
 * @date 2018/4/25 10:19
 * @return
 **/
public class NonWorkingTimeByDepartment {
    /**
     * 部门
     */
    private String department;
    /**
     * 部门人数
     */
    private String departmentNum;
    /**
     * 部门时长
     */
    private String timeByDepartment;
    /**
     * 人均时长
     */
    private String timeByPerson;

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTimeByDepartment() {
        return timeByDepartment;
    }

    public void setTimeByDepartment(String timeByDepartment) {
        this.timeByDepartment = timeByDepartment;
    }

    public String getTimeByPerson() {
        return timeByPerson;
    }

    public void setTimeByPerson(String timeByPerson) {
        this.timeByPerson = timeByPerson;
    }

    @Override
    public String toString() {
        return "NonWorkingTimeByDepartment{" +
                "department='" + department + '\'' +
                ", departmentNum='" + departmentNum + '\'' +
                ", timeByDepartment='" + timeByDepartment + '\'' +
                ", timeByPerson='" + timeByPerson + '\'' +
                '}';
    }

    public String getDepartmentNum() {
        return departmentNum;
    }

    public void setDepartmentNum(String departmentNum) {
        this.departmentNum = departmentNum;
    }

}
