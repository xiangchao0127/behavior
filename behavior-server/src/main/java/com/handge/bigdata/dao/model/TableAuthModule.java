package com.handge.bigdata.dao.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by DaLu Guo on 2018/6/19.
 */
@Entity
@Table(name = "web_module")
public class TableAuthModule implements Serializable{

    private static final long serialVersionUID = 8897406182006999007L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

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

    @Override
    public String toString() {
        return "TableAuthModule{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
