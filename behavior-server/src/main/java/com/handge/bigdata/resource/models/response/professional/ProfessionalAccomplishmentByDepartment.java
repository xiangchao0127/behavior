package com.handge.bigdata.resource.models.response.professional;

/**
 * 部门职业素养
 * Created by MaJianfu on 2018/6/12.
 */
public class ProfessionalAccomplishmentByDepartment {
    /**
     * 排名
     */
    private String num;
    /**
     * 部门名称
     */
    private String department;
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

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "ProfessionalAccomplishmentByDepartment{" +
                "num='" + num + '\'' +
                ", department='" + department + '\'' +
                ", score='" + score + '\'' +
                '}';
    }
}

