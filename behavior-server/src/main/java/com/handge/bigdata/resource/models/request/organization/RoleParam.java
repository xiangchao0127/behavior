package com.handge.bigdata.resource.models.request.organization;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @author Liujuhao
 * @date 2018/6/19.
 */
public class RoleParam implements Serializable {

    private static final long serialVersionUID = -4938702756730324217L;

    private String roleId;

    @NotEmpty
    private String roleName;

    @NotEmpty
    private String department;

    private String roleDescription;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }

    @Override
    public String toString() {
        return "RoleParam{" +
                "roleName='" + roleName + '\'' +
                ", department='" + department + '\'' +
                ", roleDescription='" + roleDescription + '\'' +
                '}';
    }
}
