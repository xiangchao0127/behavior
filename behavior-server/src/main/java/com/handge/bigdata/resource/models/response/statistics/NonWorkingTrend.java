package com.handge.bigdata.resource.models.response.statistics;

/**
 * 工作无关上网趋势
 *
 * @param
 * @author MaJianfu
 * @date 2018/4/25 10:22
 * @return
 **/
public class NonWorkingTrend {
    /**
     * 月
     */
    private String month;
    /**
     * 周
     */
    private String week;
    /**
     * 天
     */
    private String day;
    /**
     * 非工作上网人数
     */
    private String numOfPerson;
    /**
     * 非工作上网人数百分比
     */
    private String ratioOfPerson;
    /**
     * 非工作上网时长
     */
    private String nonWorkingTime;
    /**
     * 工作相关上网时长
     */
    private String workingTime;

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getNumOfPerson() {
        return numOfPerson;
    }

    public void setNumOfPerson(String numOfPerson) {
        this.numOfPerson = numOfPerson;
    }

    public String getRatioOfPerson() {
        return ratioOfPerson;
    }

    public void setRatioOfPerson(String ratioOfPerson) {
        this.ratioOfPerson = ratioOfPerson;
    }

    public String getNonWorkingTime() {
        return nonWorkingTime;
    }

    public void setNonWorkingTime(String nonWorkingTime) {
        this.nonWorkingTime = nonWorkingTime;
    }

    public String getWorkingTime() {
        return workingTime;
    }

    public void setWorkingTime(String workingTime) {
        this.workingTime = workingTime;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return "NonWorkingTrend{" +
                "month='" + month + '\'' +
                ", week='" + week + '\'' +
                ", day='" + day + '\'' +
                ", numOfPerson='" + numOfPerson + '\'' +
                ", ratioOfPerson='" + ratioOfPerson + '\'' +
                ", nonWorkingTime='" + nonWorkingTime + '\'' +
                ", workingTime='" + workingTime + '\'' +
                '}';
    }
}
