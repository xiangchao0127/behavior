package com.handge.bigdata.resource.models.response.statistics;

/***
 *
 * @author XiangChao
 * @date 2018/5/22 16:30
 * @param
 * @return
 **/
public class UnrelatedTime {

    /**
     * 分类
     */
    private String appClass;
    /**
     * 分类上网平均时长
     */
    private String countTime;


    public String getAppClass() {
        return appClass;
    }

    public void setAppClass(String appClass) {
        this.appClass = appClass;
    }

    public String getCountTime() {
        return countTime;
    }

    public void setCountTime(String countTime) {
        this.countTime = countTime;
    }

    @Override
    public String toString() {
        return "UnrelatedTime{" +
                "appClass='" + appClass + '\'' +
                ", countTime='" + countTime + '\'' +
                '}';
    }
}
