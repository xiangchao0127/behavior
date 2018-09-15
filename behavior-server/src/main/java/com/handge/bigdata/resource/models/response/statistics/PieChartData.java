package com.handge.bigdata.resource.models.response.statistics;

/**
 * @author XiangChao
 * @date 2018/5/16 17:52
 **/
public class PieChartData {
    /**
     * 标签名
     */
    private String appTag;

    /**
     * 百分比
     */
    private String ratio;
    /**
     * 时长(分钟)
     */
    private String duration;

    public String getAppTag() {
        return appTag;
    }

    public void setAppTag(String appTag) {
        this.appTag = appTag;
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "PieChartData{" +
                "appTag='" + appTag + '\'' +
                ", ratio='" + ratio + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }
}
