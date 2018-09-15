package com.handge.bigdata.dao.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * @author Liujuhao
 * @date 2018/6/4.
 */
@Entity
@Table(name = "auth_role_basic")
public class TableAuthRoleBasic implements Serializable {

    private static final long serialVersionUID = -8638415633648576237L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "role_name")
    private String roleName;

    @Column(name = "description")
    private String description;

    @Column(name ="role_property")
    private String roleProperty;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "middle_higher_lower_role", joinColumns = {@JoinColumn(name = "lower_role_id")}, inverseJoinColumns = {@JoinColumn(name = "higher_role_id")})
    private Set<TableAuthRoleBasic> lowerRoleList;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "middle_higher_lower_role", joinColumns = {@JoinColumn(name = "higher_role_id")}, inverseJoinColumns = {@JoinColumn(name = "lower_role_id")})
    private Set<TableAuthRoleBasic> higherRoleList;

    @ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(name = "middle_account_role", joinColumns = {@JoinColumn(name = "role_id")}, inverseJoinColumns = {@JoinColumn(name = "account_id")})
    private Set<TableAuthAccount> accountList;

    @ManyToMany(/*fetch = FetchType.EAGER, */cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "middle_role_permission", joinColumns = {@JoinColumn(name = "role_id")}, inverseJoinColumns = {@JoinColumn(name = "permission_id")})
    private Set<TableAuthPermission> permissionList;

    @ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(name = "middle_role_department", joinColumns = {@JoinColumn(name = "role_id")}, inverseJoinColumns = {@JoinColumn(name = "department_id")})
    private Set<TableEntityDepartment> leadDepartmentList;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "department_id")
    private TableEntityDepartment department;

    public long getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Set<TableAuthRoleBasic> getLowerRoleList() {
        return lowerRoleList;
    }

    public void setLowerRoleList(Set<TableAuthRoleBasic> lowerRoleList) {
        this.lowerRoleList = lowerRoleList;
    }

    public Set<TableAuthRoleBasic> getHigherRoleList() {
        return higherRoleList;
    }

    public void setHigherRoleList(Set<TableAuthRoleBasic> higherRoleList) {
        this.higherRoleList = higherRoleList;
    }

    public Set<TableAuthAccount> getAccountList() {
        return accountList;
    }

    public void setAccountList(Set<TableAuthAccount> accountList) {
        this.accountList = accountList;
    }

    public Set<TableAuthPermission> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(Set<TableAuthPermission> permissionList) {
        this.permissionList = permissionList;
    }

    public Set<TableEntityDepartment> getLeadDepartmentList() {
        return leadDepartmentList;
    }

    public void setLeadDepartmentList(Set<TableEntityDepartment> leadDepartmentList) {
        this.leadDepartmentList = leadDepartmentList;
    }

    public TableEntityDepartment getDepartment() {
        return department;
    }

    public void setDepartment(TableEntityDepartment department) {
        this.department = department;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRoleProperty() {
        return roleProperty;
    }

    public void setRoleProperty(String roleProperty) {
        this.roleProperty = roleProperty;
    }

    //    @Override
//    public String toString() {
//        return "TableAuthRoleBasic{" +
//                "id=" + id +
//                ", roleName='" + roleName + '\'' +
//                ", lowerRoleId='" + lowerRoleId + '\'' +
//                ", higherRoleId='" + higherRoleId + '\'' +
//                ", employeeList=" + employeeList +
//                ", permissionList=" + permissionList +
//                '}';
//    }
}
