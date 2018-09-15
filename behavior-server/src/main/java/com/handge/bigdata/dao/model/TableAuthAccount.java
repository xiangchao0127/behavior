package com.handge.bigdata.dao.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * @author Liujuhao
 * @date 2018/6/6.
 */

@Entity
@Table(name = "auth_account")
public class TableAuthAccount implements Serializable {

    private static final long serialVersionUID = 6981023158947597464L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "salt")
    private String salt;

    @Column(name = "status")
    private String status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "employee_id", unique = true)
    private TableEntityEmployee employee;

    @ManyToMany/*(fetch = FetchType.EAGER)*/
    @JoinTable(name = "middle_account_role", joinColumns = {@JoinColumn(name = "account_id")}, inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private Set<TableAuthRoleBasic> roleList;

    @OneToMany(mappedBy = "account")
    private Set<TableEntityDevice> deviceList;

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TableEntityEmployee getEmployee() {
        return employee;
    }

    public void setEmployee(TableEntityEmployee employee) {
        this.employee = employee;
    }

    public Set<TableAuthRoleBasic> getRoleList() {
        return roleList;
    }

    public void setRoleList(Set<TableAuthRoleBasic> roleList) {
        this.roleList = roleList;
    }

    public Set<TableEntityDevice> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(Set<TableEntityDevice> deviceList) {
        this.deviceList = deviceList;
    }
}
