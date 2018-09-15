package com.handge.bigdata.dao.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author liuqian
 * @date 2018/6/25
 * @Description:
 */
@Entity
@Table(name = "config_extend")
public class TableConfigExtend implements Serializable {
    private static final long serialVersionUID = -6107800242558613057L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "_region")
    private String region;

    @Column(name = "_key")
    private String key;

    @Column(name = "_value")
    private String value;

    @Column(name = "desc")
    private String desc;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "TableConfigExtend{" +
                "id=" + id +
                ", region='" + region + '\'' +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
