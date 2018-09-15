package com.handge.bigdata.resource.models.response.professional;

/**
 * create by xc on 2018/6/15
 */
public class ScoreDistributionTrend {
    /**
     * 开始分数
     */
    private String startScore;
    /**
     * 人数
     */
    private String numberOfPeople;

    public String getStartScore() {
        return startScore;
    }

    public void setStartScore(String startScore) {
        this.startScore = startScore;
    }

    public String getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(String numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    @Override
    public String toString() {
        return "ScoreDistributionTrend{" +
                "startScore='" + startScore + '\'' +
                ", numberOfPeople='" + numberOfPeople + '\'' +
                '}';
    }
}
