package com.handge.bigdata.resource.models.response.statistics;

import java.util.ArrayList;

/**
 * @author XiangChao
 * @date 2018/5/16 17:54
 **/
public class PieChartDatas {

    private ArrayList<PieChartData> pieChartDataList;

    public ArrayList<PieChartData> getPieChartDataList() {
        return pieChartDataList;
    }

    public void setPieChartDataList(ArrayList<PieChartData> pieChartDataList) {
        this.pieChartDataList = pieChartDataList;
    }

    @Override
    public String toString() {
        return "PieChartDatas{" +
                "pieChartDataList=" + pieChartDataList +
                '}';
    }
}
