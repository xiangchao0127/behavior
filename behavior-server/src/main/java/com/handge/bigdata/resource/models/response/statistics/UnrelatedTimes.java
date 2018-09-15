package com.handge.bigdata.resource.models.response.statistics;

import java.util.ArrayList;

/***
 *
 * @author XiangChao
 * @date 2018/5/22 16:30  
 * @param
 * @return
 **/
public class UnrelatedTimes {
    /**
     * 时段(小时)
     */
    private String hour;

    /**
     * 无关上网平均时长
     */
    private String nonWorkingTime;

    private ArrayList<UnrelatedTime> UnrelatedTimes;

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getNonWorkingTime() {
        return nonWorkingTime;
    }

    public void setNonWorkingTime(String nonWorkingTime) {
        this.nonWorkingTime = nonWorkingTime;
    }

    public ArrayList<UnrelatedTime> getUnrelatedTimes() {
        return UnrelatedTimes;
    }

    public void setUnrelatedTimes(ArrayList<UnrelatedTime> unrelatedTimes) {
        UnrelatedTimes = unrelatedTimes;
    }

    @Override
    public String toString() {
        return "UnrelatedTimes{" +
                "hour='" + hour + '\'' +
                ", nonWorkingTime='" + nonWorkingTime + '\'' +
                ", UnrelatedTimes=" + UnrelatedTimes +
                '}';
    }
}
