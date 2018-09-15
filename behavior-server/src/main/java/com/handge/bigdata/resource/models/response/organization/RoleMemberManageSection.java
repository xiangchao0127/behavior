package com.handge.bigdata.resource.models.response.organization;

import java.io.Serializable;

/**
 * @author Liujuhao
 * @date 2018/6/19.
 */
public class RoleMemberManageSection implements Serializable{

    private static final long serialVersionUID = -2531151268455099228L;

    private long id;

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

    public RoleMemberManageSection(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public RoleMemberManageSection() {
    }

    @Override
    public String toString() {
        return "RoleMemberManageSection{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
