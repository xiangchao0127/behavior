package com.handge.bigdata.resource.models.response.monitor;


/**
 * @author Guo Dalu
 * @date 2018/4/25
 */
public class FlowOfNonWorkingApp {
    /**
     * 应用名称
     */
    private String appName;
    /**
     * 上传流量
     */
    private String uploadFlow;
    /**
     * 下载流量
     */
    private String downloadFlow;
    /**
     * 总流量
     */
    private String totalFlow;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
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

    public String getTotalFlow() {
        return totalFlow;
    }

    public void setTotalFlow(String totalFlow) {
        this.totalFlow = totalFlow;
    }

    @Override
    public String toString() {
        return "FlowOfNonWorkingApp{" +
                "appName='" + appName + '\'' +
                ", uploadFlow=" + uploadFlow +
                ", downloadFlow=" + downloadFlow +
                ", totalFlow=" + totalFlow +
                '}';
    }
}
