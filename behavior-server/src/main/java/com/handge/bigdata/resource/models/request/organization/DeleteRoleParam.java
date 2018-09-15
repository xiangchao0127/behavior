package com.handge.bigdata.resource.models.request.organization;

import java.io.Serializable;
import java.util.List;

/**
 * @author Liujuhao
 * @date 2018/6/19.
 */
public class DeleteRoleParam implements Serializable {

    private static final long serialVersionUID = -923583492501242605L;

    private List<String> roleIds;

    public List<String> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<String> roleIds) {
        this.roleIds = roleIds;
    }
}
