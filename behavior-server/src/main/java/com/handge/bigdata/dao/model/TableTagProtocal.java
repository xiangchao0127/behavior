package com.handge.bigdata.dao.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by XC on 2018/6/25.
 */
@Entity
@Table(name = "tag_protocal")
public class TableTagProtocal implements Serializable {
    private static final long serialVersionUID = 8463042186569741596L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "protocol")
    private String protocol;

    @Column(name = "tag")
    private String tag;

    @Column(name = "appname")
    private String appname;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    @Override
    public String toString() {
        return "TableTagProtocal{" +
                "id=" + id +
                ", protocol='" + protocol + '\'' +
                ", tag='" + tag + '\'' +
                ", appname='" + appname + '\'' +
                '}';
    }
}
