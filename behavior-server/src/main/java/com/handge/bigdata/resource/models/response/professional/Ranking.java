package com.handge.bigdata.resource.models.response.professional;

/**
 * 行为素养员工排名返回值
 * create by xc on 2018/6/13
 */
public class Ranking {
    /**
     * 员工排名（百分数）
     */
    private String overPercent;
    /**
     * 公司排名
     */
    private String rankingCompany;
    /**
     * 公司人数
     */
    private String countCompany;
    /**
     * 部门排名
     */
    private String rankingDepartment;
    /**
     * 部门人数
     */
    private String countDepartment;

    public String getOverPercent() {
        return overPercent;
    }

    public void setOverPercent(String overPercent) {
        this.overPercent = overPercent;
    }

    public String getRankingCompany() {
        return rankingCompany;
    }

    public void setRankingCompany(String rankingCompany) {
        this.rankingCompany = rankingCompany;
    }

    public String getCountCompany() {
        return countCompany;
    }

    public void setCountCompany(String countCompany) {
        this.countCompany = countCompany;
    }

    public String getRankingDepartment() {
        return rankingDepartment;
    }

    public void setRankingDepartment(String rankingDepartment) {
        this.rankingDepartment = rankingDepartment;
    }

    public String getCountDepartment() {
        return countDepartment;
    }

    public void setCountDepartment(String countDepartment) {
        this.countDepartment = countDepartment;
    }

    @Override
    public String toString() {
        return "Ranking{" +
                "overPercent='" + overPercent + '\'' +
                ", rankingCompany='" + rankingCompany + '\'' +
                ", countCompany='" + countCompany + '\'' +
                ", rankingDepartment='" + rankingDepartment + '\'' +
                ", countDepartment='" + countDepartment + '\'' +
                '}';
    }
}
