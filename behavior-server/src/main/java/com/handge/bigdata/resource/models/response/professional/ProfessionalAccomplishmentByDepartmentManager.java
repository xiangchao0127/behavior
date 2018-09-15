package com.handge.bigdata.resource.models.response.professional;

import com.handge.bigdata.annotation.Hiding;

/**
 * 部门经理职业素养
 * Created by MaJianfu on 2018/6/12.
 */
public class ProfessionalAccomplishmentByDepartmentManager {
    /**
     * 排名
     */
    private String num;
    /**
     * 部门名称
     */
    private String department;
    /**
     * 工号
     */
    private String number;
    /**
     * 姓名
     */
    private String name;
    /**
     * 综合分数
     */
    private String score;

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
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

    public String getName() {
        return name;
    }

    @Hiding
    public void setName(String name) {
        this.name = name;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "ProfessionalAccomplishmentByDepartmentManager{" +
                "num='" + num + '\'' +
                ", department='" + department + '\'' +
                ", number='" + number + '\'' +
                ", name='" + name + '\'' +
                ", score='" + score + '\'' +
                '}';
    }
}
