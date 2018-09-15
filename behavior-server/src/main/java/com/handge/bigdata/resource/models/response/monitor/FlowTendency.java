package com.handge.bigdata.resource.models.response.monitor;

import java.util.List;

/**
 * @author Guo Dalu
 * @date 2018/4/25
 */
public class FlowTendency {
    /**
     * 时间
     */
    private String time;
    /**
     * 总流量
     */
    private String totalFlow;
    /**
     * 上传流量
     */
    private String uploadFlow;
    /**
     * 下载流量
     */
    private String downloadFlow;

    /**
     * 无关应用流量信息
     */
    private List<FlowOfNonWorkingApp> flowOfNonWorkingAppList;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTotalFlow() {
        return totalFlow;
    }

    public void setTotalFlow(String totalFlow) {
        this.totalFlow = totalFlow;
    }

    public String getUploadFlow() {
        return uploadFlow;
    }

    public void setUploadFlow(String uploadFlow) {
        this.uploadFlow = uploadFlow;
    }

    public String getDownloadFlow() {
        return downloadFlow;
    }

    public void setDownloadFlow(String downloadFlow) {
        this.downloadFlow = downloadFlow;
    }

    public List<FlowOfNonWorkingApp> getFlowOfNonWorkingAppList() {
        return flowOfNonWorkingAppList;
    }

    public void setFlowOfNonWorkingAppList(List<FlowOfNonWorkingApp> flowOfNonWorkingAppList) {
        this.flowOfNonWorkingAppList = flowOfNonWorkingAppList;
    }

    @Override
    public String toString() {
        return "FlowTendency{" +
                "time=" + time +
                ", totalFlow=" + totalFlow +
                ", uploadFlow=" + uploadFlow +
                ", downloadFlow=" + downloadFlow +
                ", flowOfNonWorkingAppList=" + flowOfNonWorkingAppList +
                '}';
    }
}
