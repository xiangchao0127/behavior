package com.handge.bigdata.dao.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * @author Liujuhao
 * @date 2018/6/8.
 */

@Entity
@Table(name = "entity_department_information_basic")
public class TableEntityDepartment implements Serializable {

    private static final long serialVersionUID = 4687851989530962251L;
    @Id
    @Column(name = "department_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "department_name")
    private String departmentName;

    @Column(name = "create_at")
    private Date createAt;

    @Column(name = "department_header")
    private String departmentHeader;

    @Column(name = "description")
    private String description;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "middle_role_department", joinColumns = {@JoinColumn(name = "department_id")}, inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private Set<TableAuthRoleBasic> leadRoleList;

    @OneToMany(mappedBy = "department")
    private Set<TableEntityEmployee> employeeList;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "middle_higher_lower_department", joinColumns = {@JoinColumn(name = "lower_department_id")}, inverseJoinColumns = {@JoinColumn(name = "higher_department_id")})
    private Set<TableEntityDepartment> higherDepartmentList;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinTable(name = "middle_higher_lower_department", joinColumns = {@JoinColumn(name = "higher_department_id")}, inverseJoinColumns = {@JoinColumn(name = "lower_department_id")})
    private Set<TableEntityDepartment> lowerDepartmentList;

    @OneToMany(mappedBy = "department")
    private Set<TableAuthRoleBasic> roleList;

    public long getId() {
        return id;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getDepartmentHeader() {
        return departmentHeader;
    }

    public void setDepartmentHeader(String departmentHeader) {
        this.departmentHeader = departmentHeader;
    }

    public Set<TableAuthRoleBasic> getLeadRoleList() {
        return leadRoleList;
    }

    public void setLeadRoleList(Set<TableAuthRoleBasic> leadRoleList) {
        this.leadRoleList = leadRoleList;
    }

    public Set<TableEntityEmployee> getEmployeeList() {
        return employeeList;
    }

    public void setEmployeeList(Set<TableEntityEmployee> employeeList) {
        this.employeeList = employeeList;
    }

    public Set<TableEntityDepartment> getHigherDepartmentList() {
        return higherDepartmentList;
    }

    public void setHigherDepartmentList(Set<TableEntityDepartment> higherDepartmentList) {
        this.higherDepartmentList = higherDepartmentList;
    }

    public Set<TableEntityDepartment> getLowerDepartmentList() {
        return lowerDepartmentList;
    }

    public void setLowerDepartmentList(Set<TableEntityDepartment> lowerDepartmentList) {
        this.lowerDepartmentList = lowerDepartmentList;
    }

    public Set<TableAuthRoleBasic> getRoleList() {
        return roleList;
    }

    public void setRoleList(Set<TableAuthRoleBasic> roleList) {
        this.roleList = roleList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
