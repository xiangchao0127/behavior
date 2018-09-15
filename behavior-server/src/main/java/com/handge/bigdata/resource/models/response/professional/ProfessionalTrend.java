package com.handge.bigdata.resource.models.response.professional;

/**
 * 职业素养趋势response
 * Created by xc on 2018/6/12.
 */
public class ProfessionalTrend {
    /**
     * 姓名
     */
    private String name;
    /**
     * 月份
     */
    private String month;
    /**
     * 个人月平均分
     */
    private String personAvgScore;
    /**
     * 部门月平均分
     */
    private String departmentAvgScore;
    /**
     * 公司月平均分
     */
    private String companyAvgScore;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getPersonAvgScore() {
        return personAvgScore;
    }

    public void setPersonAvgScore(String personAvgScore) {
        this.personAvgScore = personAvgScore;
    }

    public String getDepartmentAvgScore() {
        return departmentAvgScore;
    }

    public void setDepartmentAvgScore(String departmentAvgScore) {
        this.departmentAvgScore = departmentAvgScore;
    }

    public String getCompanyAvgScore() {
        return companyAvgScore;
    }

    public void setCompanyAvgScore(String companyAvgScore) {
        this.companyAvgScore = companyAvgScore;
    }

    @Override
    public String toString() {
        return "ProfessionalTrend{" +
                "name='" + name + '\'' +
                ", month='" + month + '\'' +
                ", personAvgScore='" + personAvgScore + '\'' +
                ", departmentAvgScore='" + departmentAvgScore + '\'' +
                ", companyAvgScore='" + companyAvgScore + '\'' +
                '}';
    }
}
