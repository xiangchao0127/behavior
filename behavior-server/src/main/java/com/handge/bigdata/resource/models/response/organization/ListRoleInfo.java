package com.handge.bigdata.resource.models.response.organization;

import java.io.Serializable;
import java.util.List;

/**
 * @author Liujuhao
 * @date 2018/6/20.
 */
public class ListRoleInfo implements Serializable{

    private static final long serialVersionUID = -5365723745132947714L;

    private String id;

    private String roleName;

    private String departmentName;

    private String roleDescription;

    private List<String> members;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "ListRoleInfo{" +
                "id='" + id + '\'' +
                ", roleName='" + roleName + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", roleDescription='" + roleDescription + '\'' +
                ", members=" + members +
                '}';
    }
}
