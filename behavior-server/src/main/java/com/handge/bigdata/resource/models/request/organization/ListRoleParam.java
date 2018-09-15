package com.handge.bigdata.resource.models.request.organization;

import com.handge.bigdata.resource.models.request.base.PageParam;

import java.io.Serializable;

/**
 * @author Liujuhao
 * @date 2018/6/19.
 */
public class ListRoleParam extends PageParam implements Serializable{

    private static final long serialVersionUID = 7151234458549191445L;

    private String roleName;

    private String departmentName;

    private String roleDescription;

    private String departmentMember;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }

    public String getDepartmentMember() {
        return departmentMember;
    }

    public void setDepartmentMember(String departmentMember) {
        this.departmentMember = departmentMember;
    }

    @Override
    public String toString() {
        return "ListRoleParam{" +
                "roleName='" + roleName + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", roleDescription='" + roleDescription + '\'' +
                ", departmentMember='" + departmentMember + '\'' +
                '}';
    }
}
