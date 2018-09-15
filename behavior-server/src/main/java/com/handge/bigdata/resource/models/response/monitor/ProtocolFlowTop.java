package com.handge.bigdata.resource.models.response.monitor;

/**
 * @author Guo Dalu
 * @date 2018/4/25
 */
public class ProtocolFlowTop {
    /**
     * 协议名
     */
    private String protocol;
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
     * 消耗最高流量的应用
     */
    private String maxFlowApp;

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
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

    public String getMaxFlowApp() {
        return maxFlowApp;
    }

    public void setMaxFlowApp(String maxFlowApp) {
        this.maxFlowApp = maxFlowApp;
    }

    @Override
    public String toString() {
        return "ProtocolFlowTop{" +
                "protocol='" + protocol + '\'' +
                ", totalFlow=" + totalFlow +
                ", uploadFlow=" + uploadFlow +
                ", downloadFlow=" + downloadFlow +
                ", maxFlowApp='" + maxFlowApp + '\'' +
                '}';
    }
}
