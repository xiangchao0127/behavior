package com.handge.bigdata.dao.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author liuqian
 * @date 2018/6/25
 * @Description:
 */
@Entity
@Table(name = "lib_illegal_rules")
public class TableLibIllegalRules implements Serializable {
    private static final long serialVersionUID = -8582153578596497252L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "level")
    private String level;

    @Column(name = "type")
    private String type;

    @Column(name = "class")
    private String clazz;

    @Column(name = "proto")
    private String proto;

    @Column(name = "domain")
    private String domain;

    @Column(name = "monitor_range")
    private String monitorRange;

    @Column(name = "monitor_start_time")
    private Date monitorStartTime;

    @Column(name = "monitor_end_time")
    private Date monitorEndTime;

    @Column(name = "status")
    private long status;

    @Column(name = "create_at")
    private Date createAt;

    @Column(name = "creator")
    private String creator;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getProto() {
        return proto;
    }

    public void setProto(String proto) {
        this.proto = proto;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getMonitorRange() {
        return monitorRange;
    }

    public void setMonitorRange(String monitorRange) {
        this.monitorRange = monitorRange;
    }

    public Date getMonitorStartTime() {
        return monitorStartTime;
    }

    public void setMonitorStartTime(Date monitorStartTime) {
        this.monitorStartTime = monitorStartTime;
    }

    public Date getMonitorEndTime() {
        return monitorEndTime;
    }

    public void setMonitorEndTime(Date monitorEndTime) {
        this.monitorEndTime = monitorEndTime;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Override
    public String toString() {
        return "TableLibIllegalRules{" +
                "id=" + id +
                ", level='" + level + '\'' +
                ", type='" + type + '\'' +
                ", clazz='" + clazz + '\'' +
                ", proto='" + proto + '\'' +
                ", domain='" + domain + '\'' +
                ", monitorRange='" + monitorRange + '\'' +
                ", monitorStartTime=" + monitorStartTime +
                ", monitorEndTime=" + monitorEndTime +
                ", status=" + status +
                ", createAt=" + createAt +
                ", creator='" + creator + '\'' +
                '}';
    }
}
