package com.handge.bigdata.dao.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author liuqian
 * @date 2018/6/25
 * @Description:
 */
@Entity
@Table(name = "dict_country_basic")
public class TableDictCountryBasic implements Serializable{
    private static final long serialVersionUID = 1887423912385688181L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "nick_name")
    private String nickName;

    @Column(name = "capital_geo")
    private String capitalGeo;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getCapitalGeo() {
        return capitalGeo;
    }

    public void setCapitalGeo(String capitalGeo) {
        this.capitalGeo = capitalGeo;
    }

    @Override
    public String toString() {
        return "TableDictCountryBasic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", nickName='" + nickName + '\'' +
                ", capitalGeo='" + capitalGeo + '\'' +
                '}';
    }
}
