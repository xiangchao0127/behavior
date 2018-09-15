package com.handge.bigdata.resource.models.request.organization;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * @author Liujuhao
 * @date 2018/6/19.
 */
public class RoleMemberParam implements Serializable {

    private static final long serialVersionUID = -1451327457093314779L;

    @NotEmpty
    private String roleId;

    @Size(min = 1)
    private List<String> accountList;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public List<String> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<String> accountList) {
        this.accountList = accountList;
    }

    @Override
    public String toString() {
        return "RoleMemberParam{" +
                "roleId='" + roleId + '\'' +
                ", accountList=" + accountList +
                '}';
    }
}
