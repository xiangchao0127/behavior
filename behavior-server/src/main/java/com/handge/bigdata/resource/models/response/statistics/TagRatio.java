package com.handge.bigdata.resource.models.response.statistics;

import java.util.List;

/***
 * 应用占比
 * @author MaJianfu
 * @date 2018/4/25 10:10
 * @param
 * @return
 **/
public class TagRatio {
    /**
     * 标签名称
     */
    private String tagName;
    /**
     * 非工作各标签应用使用占比
     */
    private String ratio;
    /**
     * 使用非工作各标签应用总人数
     */
    private String totalNum;
    /**
     * 实际使用该标签下应用的人数
     */
    private String tagNum;
    /**
     * 具体的工作无关应用
     */
    private List<NonWorkingApp> nonWorkingApps;

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    public String getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(String totalNum) {
        this.totalNum = totalNum;
    }

    public String getTagNum() {
        return tagNum;
    }

    public void setTagNum(String tagNum) {
        this.tagNum = tagNum;
    }

    public List<NonWorkingApp> getNonWorkingApps() {
        return nonWorkingApps;
    }

    public void setNonWorkingApps(List<NonWorkingApp> nonWorkingApps) {
        this.nonWorkingApps = nonWorkingApps;
    }

    @Override
    public String toString() {
        return "TagRatio{" +
                "tagName='" + tagName + '\'' +
                ", ratio=" + ratio +
                ", totalNum=" + totalNum +
                ", tagNum=" + tagNum +
                ", nonWorkingApps=" + nonWorkingApps +
                '}';
    }
}
