package com.handge.bigdata.resource.models.request.organization;

import java.io.Serializable;

/**
 * @author Liujuhao
 * @date 2018/6/20.
 */
public class ListMemberByRoleParam implements Serializable{

    private static final long serialVersionUID = 6833387367400861933L;

    private String roleId;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
}
