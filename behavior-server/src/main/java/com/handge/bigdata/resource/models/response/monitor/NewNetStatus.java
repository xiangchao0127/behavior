package com.handge.bigdata.resource.models.response.monitor;

import java.util.ArrayList;

public class NewNetStatus {
    /**
     * 统计总人数
     */
    private String numOfAll;

    private ArrayList<SubNetStatus> subNetStatuList;

    public String getNumOfAll() {
        return numOfAll;
    }

    public void setNumOfAll(String numOfAll) {
        this.numOfAll = numOfAll;
    }

    public ArrayList<SubNetStatus> getSubNetStatuList() {
        return subNetStatuList;
    }

    public void setSubNetStatuList(ArrayList<SubNetStatus> subNetStatuList) {
        this.subNetStatuList = subNetStatuList;
    }

    @Override
    public String toString() {
        return "NewNetStatus{" +
                "numOfAll='" + numOfAll + '\'' +
                ", subNetStatuList=" + subNetStatuList +
                '}';
    }
}
