package com.handge.bigdata.resource.models.response.monitor;

/**
 * 非工作性上网应用
 *
 * @author liuqian
 * @date 2018/4/25
 */
public class NonWorkingAppTop {
    /**
     * 应用
     */
    private String appName;
    /**
     * 非工作应用占比
     */
    private String ratioOfNonWorkingApp;
    /**
     * 公司人数
     */
    private String numOfAllPeople;
    /**
     * 访问人数
     */
    private String numOfVisit;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getRatioOfNonWorkingApp() {
        return ratioOfNonWorkingApp;
    }

    public void setRatioOfNonWorkingApp(String ratioOfNonWorkingApp) {
        this.ratioOfNonWorkingApp = ratioOfNonWorkingApp;
    }

    public String getNumOfAllPeople() {
        return numOfAllPeople;
    }

    public void setNumOfAllPeople(String numOfAllPeople) {
        this.numOfAllPeople = numOfAllPeople;
    }

    public String getNumOfVisit() {
        return numOfVisit;
    }

    public void setNumOfVisit(String numOfVisit) {
        this.numOfVisit = numOfVisit;
    }

    @Override
    public String toString() {
        return "NonWorkingAppTop{" +
                "appName='" + appName + '\'' +
                ", ratioOfNonWorkingApp=" + ratioOfNonWorkingApp +
                ", numOfAllPeople=" + numOfAllPeople +
                ", numOfVisit=" + numOfVisit +
                '}';
    }
}
