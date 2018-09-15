package com.handge.bigdata.resource.models.response.professional;

import java.util.ArrayList;

/**
 * create by xc on 2018/6/15
 */
public class ScoreDistributionTrendMaker {
    /**
     * 公司或部门
     */
    private String mode;

    private ArrayList<ScoreDistributionTrend> scoreDistributionTrends;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public ArrayList<ScoreDistributionTrend> getScoreDistributionTrends() {
        return scoreDistributionTrends;
    }

    public void setScoreDistributionTrends(ArrayList<ScoreDistributionTrend> scoreDistributionTrends) {
        this.scoreDistributionTrends = scoreDistributionTrends;
    }

    @Override
    public String toString() {
        return "ScoreDistributionTrendMaker{" +
                "mode='" + mode + '\'' +
                ", scoreDistributionTrends=" + scoreDistributionTrends +
                '}';
    }
}
