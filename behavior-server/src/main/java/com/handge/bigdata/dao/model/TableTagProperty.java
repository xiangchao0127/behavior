package com.handge.bigdata.dao.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by XC on 2018/6/25.
 */
@Entity
@Table(name = "tag_property")
public class TableTagProperty implements Serializable {

    private static final long serialVersionUID = 8463042186569741596L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long app_id;

    @Column(name = "tag_name")
    private String tagName;

    @Column(name = "property")
    private String property;


    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public long getApp_id() {
        return app_id;
    }

    public void setApp_id(long app_id) {
        this.app_id = app_id;
    }

    @Override
    public String toString() {
        return "TableTagProperty{" +
                "app_id=" + app_id +
                ", tagName='" + tagName + '\'' +
                ", property='" + property + '\'' +
                '}';
    }
}

