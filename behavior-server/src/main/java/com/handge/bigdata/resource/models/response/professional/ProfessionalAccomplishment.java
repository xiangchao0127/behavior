package com.handge.bigdata.resource.models.response.professional;

/**
 * Created by DaLu Guo on 2018/6/14.
 */
public class ProfessionalAccomplishment {
    /**
     * 平均得分
     */
    private String comprehensiveScore;
    /**
     * 忠诚度
     */
    private String loyalty;
    /**
     * 工作态度
     */
    private String workingAttitude;
    /**
     * 遵规守纪程度
     */
    private String complianceDiscipline;

    public String getComplianceDiscipline() {
        return complianceDiscipline;
    }

    public void setComplianceDiscipline(String complianceDiscipline) {
        this.complianceDiscipline = complianceDiscipline;
    }

    public String getComprehensiveScore() {
        return comprehensiveScore;
    }

    public void setComprehensiveScore(String comprehensiveScore) {
        this.comprehensiveScore = comprehensiveScore;
    }

    public String getLoyalty() {
        return loyalty;
    }

    public void setLoyalty(String loyalty) {
        this.loyalty = loyalty;
    }

    public String getWorkingAttitude() {
        return workingAttitude;
    }

    public void setWorkingAttitude(String workingAttitude) {
        this.workingAttitude = workingAttitude;
    }

    @Override
    public String toString() {
        return "ProfessionalAccomplishment{" +
                "comprehensiveScore='" + comprehensiveScore + '\'' +
                ", loyalty='" + loyalty + '\'' +
                ", workingAttitude='" + workingAttitude + '\'' +
                ", complianceDiscipline='" + complianceDiscipline + '\'' +
                '}';
    }
}
