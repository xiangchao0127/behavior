package com.handge.bigdata.resource.models.response.monitor;

import java.util.List;

/**
 * @author Guo Dalu
 * @date 2018/4/25
 */
public class FlowTrend {
    /**
     * 境外访问数
     */
    private String numOfOverseasVisit;
    /**
     * 在线用户数
     */
    private String numOfOnlineUser;
    /**
     * 总流量
     */
    private List<CountryVisitInfo> countryVisitInfos;

    public String getNumOfOverseasVisit() {
        return numOfOverseasVisit;
    }

    public void setNumOfOverseasVisit(String numOfOverseasVisit) {
        this.numOfOverseasVisit = numOfOverseasVisit;
    }

    public String getNumOfOnlineUser() {
        return numOfOnlineUser;
    }

    public void setNumOfOnlineUser(String numOfOnlineUser) {
        this.numOfOnlineUser = numOfOnlineUser;
    }

    public List<CountryVisitInfo> getCountryVisitInfos() {
        return countryVisitInfos;
    }

    public void setCountryVisitInfos(List<CountryVisitInfo> countryVisitInfos) {
        this.countryVisitInfos = countryVisitInfos;
    }

    @Override
    public String toString() {
        return "FlowTrend{" +
                "numOfOverseasVisit='" + numOfOverseasVisit + '\'' +
                ", numOfOnlineUser='" + numOfOnlineUser + '\'' +
                ", countryVisitInfos=" + countryVisitInfos +
                '}';
    }
}
