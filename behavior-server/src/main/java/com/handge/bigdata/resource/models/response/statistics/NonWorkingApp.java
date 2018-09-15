package com.handge.bigdata.resource.models.response.statistics;

/**
 * 非工作时间应用占比
 *
 * @author DaLu Guo
 * @date
 */
public class NonWorkingApp {
    /**
     * 占比
     */
    private String ratio;
    /**
     * 人数
     */
    private int numOfPerson;
    /**
     * 应用
     */
    private String appName;

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    public int getNumOfPerson() {
        return numOfPerson;
    }

    public void setNumOfPerson(int numOfPerson) {
        this.numOfPerson = numOfPerson;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Override
    public String toString() {
        return "NonWorkingApp{" +
                "ratio=" + ratio +
                ", numOfPerson=" + numOfPerson +
                ", appName='" + appName + '\'' +
                '}';
    }
}
