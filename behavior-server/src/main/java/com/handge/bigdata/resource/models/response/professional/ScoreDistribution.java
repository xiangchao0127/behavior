package com.handge.bigdata.resource.models.response.professional;

/**
 * create by xc on 2018/6/13
 */
public class ScoreDistribution {
    /**
     * 姓名
     */
    private String name;
    /**
     * 分数
     */
    private String score;
    /**
     * 公司分数区间集合
     */
    private ScoreDistributionTrendMaker scoreDistributionTrendComp;

    /**
     * 部门分数区间集合
     */
    private ScoreDistributionTrendMaker scoreDistributionTrendDep;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public ScoreDistributionTrendMaker getScoreDistributionTrendComp() {
        return scoreDistributionTrendComp;
    }

    public void setScoreDistributionTrendComp(ScoreDistributionTrendMaker scoreDistributionTrendComp) {
        this.scoreDistributionTrendComp = scoreDistributionTrendComp;
    }

    public ScoreDistributionTrendMaker getScoreDistributionTrendDep() {
        return scoreDistributionTrendDep;
    }

    public void setScoreDistributionTrendDep(ScoreDistributionTrendMaker scoreDistributionTrendDep) {
        this.scoreDistributionTrendDep = scoreDistributionTrendDep;
    }

    @Override
    public String toString() {
        return "ScoreDistribution{" +
                "name='" + name + '\'' +
                ", score='" + score + '\'' +
                ", scoreDistributionTrendComp=" + scoreDistributionTrendComp +
                ", scoreDistributionTrendDep=" + scoreDistributionTrendDep +
                '}';
    }
}
