package com.handge.bigdata.resource.models.response.statistics;

/**
 * 上网足迹其他信息
 * Created by MaJianfu on 2018/5/15.
 */
public class InternetFootprintInfo {
    /**
     * IP
     */
    private String ip;
    /**
     * 应用
     */
    private String app;
    /**
     * 地址
     */
    private String url;
    /**
     * 类型
     */
    private String type;
    /**
     * 性质
     */
    private String property;
    /**
     * 访问时间
     */
    private String accessTime;
    /**
     * 访问时长
     */
    private String lengthOfAccess;
    /**
     * 访问内容
     */
    private String content;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(String accessTime) {
        this.accessTime = accessTime;
    }

    public String getLengthOfAccess() {
        return lengthOfAccess;
    }

    public void setLengthOfAccess(String lengthOfAccess) {
        this.lengthOfAccess = lengthOfAccess;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "InternetFootprintInfo{" +
                ", ip='" + ip + '\'' +
                ", app='" + app + '\'' +
                ", url='" + url + '\'' +
                ", type='" + type + '\'' +
                ", property='" + property + '\'' +
                ", accessTime='" + accessTime + '\'' +
                ", lengthOfAccess='" + lengthOfAccess + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
