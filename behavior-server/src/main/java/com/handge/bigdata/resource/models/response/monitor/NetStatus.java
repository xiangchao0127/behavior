package com.handge.bigdata.resource.models.response.monitor;

import java.util.ArrayList;

/**
 * 上网情况
 *
 * @author liuqian
 * @date 2018/4/25
 */
public class NetStatus {
    /**
     * 当条记录的时间
     */
    private String time;

    private ArrayList<SubNetStatus> subNetStatuses;

    /**
     * 统计总人数
     */
    private String numOfAll;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ArrayList<SubNetStatus> getSubNetStatuses() {
        return subNetStatuses;
    }

    public void setSubNetStatuses(ArrayList<SubNetStatus> subNetStatuses) {
        this.subNetStatuses = subNetStatuses;
    }

    public String getNumOfAll() {
        return numOfAll;
    }

    public void setNumOfAll(String numOfAll) {
        this.numOfAll = numOfAll;
    }

    @Override
    public String toString() {
        return "NetStatus{" +
                "time='" + time + '\'' +
                ", subNetStatuses=" + subNetStatuses +
                ", numOfAll='" + numOfAll + '\'' +
                '}';
    }
}
