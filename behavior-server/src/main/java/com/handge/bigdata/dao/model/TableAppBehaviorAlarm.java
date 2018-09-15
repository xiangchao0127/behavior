package com.handge.bigdata.dao.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by DaLu Guo on 2018/6/19.
 */
@Entity
@Table(name =  "app_behavior_alarm")
public class TableAppBehaviorAlarm implements Serializable {
    private static final long serialVersionUID = -4018635597827293048L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "number")
    private String number;

    @Column(name = "source_ip")
    private String sourceIp;

    @Column(name = "application_name")
    private String applicationName;

    @Column(name = "application_category")
    private String applicationCategory;

    @Column(name = "application_protocol")
    private String applicationProtocol;

    @Column(name = "target_ip")
    private String targetIp;

    @Column(name = "access_time")
    private Date accessTime;

    @Column(name = "upload_flow")
    private String uploadFlow;

    @Column(name = "download_flow")
    private String downloadFlow;

    @Column(name = "detail")
    private String detail;

    @Column(name = "type")
    private String type;

    @Column(name = "level")
    private String level;

    @Column(name = "is_processed")
    private String isProcessed;

    @Column(name = "create_at")
    private Date createAt;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationCategory() {
        return applicationCategory;
    }

    public void setApplicationCategory(String applicationCategory) {
        this.applicationCategory = applicationCategory;
    }

    public String getApplicationProtocol() {
        return applicationProtocol;
    }

    public void setApplicationProtocol(String applicationProtocol) {
        this.applicationProtocol = applicationProtocol;
    }

    public String getTargetIp() {
        return targetIp;
    }

    public void setTargetIp(String targetIp) {
        this.targetIp = targetIp;
    }

    public Date getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(Date accessTime) {
        this.accessTime = accessTime;
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

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getIsProcessed() {
        return isProcessed;
    }

    public void setIsProcessed(String isProcessed) {
        this.isProcessed = isProcessed;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    @Override
    public String toString() {
        return "TableAppBehaviorAlarm{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", sourceIp='" + sourceIp + '\'' +
                ", applicationName='" + applicationName + '\'' +
                ", applicationCategory='" + applicationCategory + '\'' +
                ", applicationProtocol='" + applicationProtocol + '\'' +
                ", targetIp='" + targetIp + '\'' +
                ", accessTime=" + accessTime +
                ", uploadFlow='" + uploadFlow + '\'' +
                ", downloadFlow='" + downloadFlow + '\'' +
                ", detail='" + detail + '\'' +
                ", type=" + type +
                ", level=" + level +
                ", isProcessed=" + isProcessed +
                ", createAt=" + createAt +
                '}';
    }
}
