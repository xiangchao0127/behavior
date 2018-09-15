package com.handge.bigdata.resource.models.response.statistics;

import java.util.ArrayList;

/**
 * @param
 * @author XiangChao
 * @date 2018/5/22 20:11
 * @return
 **/
public class UnrelatedTimeAndPie {
    /**
     * 条形图
     */
    private ArrayList<UnrelatedTimes> UnrelatedTimeList;

    /**
     * 饼状图
     */
    private ArrayList<PieChartData> pieChartDataList;

    public ArrayList<UnrelatedTimes> getUnrelatedTimeList() {
        return UnrelatedTimeList;
    }

    public void setUnrelatedTimeList(ArrayList<UnrelatedTimes> unrelatedTimeList) {
        UnrelatedTimeList = unrelatedTimeList;
    }

    public ArrayList<PieChartData> getPieChartDataList() {
        return pieChartDataList;
    }

    public void setPieChartDataList(ArrayList<PieChartData> pieChartDataList) {
        this.pieChartDataList = pieChartDataList;
    }

    @Override
    public String toString() {
        return "UnrelatedTimeAndPie{" +
                "UnrelatedTimeList=" + UnrelatedTimeList +
                ", pieChartDataList=" + pieChartDataList +
                '}';
    }
}
