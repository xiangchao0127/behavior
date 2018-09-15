package com.handge.bigdata.dao.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * @author Liujuhao
 * @date 2018/6/4.
 */
@Entity
@Table(name = "auth_permission")
public class TableAuthPermission implements Serializable {

    private static final long serialVersionUID = -7333238675393388764L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "api_id", unique = true)
    private TableAuthApi api;

    @Column(name = "action")
    private String action;

    @Column(name = "pattern")
    private String pattern;

    @Column(name = "remark")
    private String remark;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "middle_role_permission", joinColumns = {@JoinColumn(name = "permission_id")}, inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private Set<TableAuthRoleBasic> roleList;

    public long getId() {
        return id;
    }

    public TableAuthApi getApi_id() {
        return api;
    }

    public void setApi_id(TableAuthApi api) {
        this.api = api;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Set<TableAuthRoleBasic> getRoleList() {
        return roleList;
    }

    public void setRoleList(Set<TableAuthRoleBasic> roleList) {
        this.roleList = roleList;
    }

    //    @Override
//    public String toString() {
//        return "TableAuthPermission{" +
//                "id=" + id +
//                ", api_id=" + api_id +
//                ", action='" + action + '\'' +
//                ", pattern='" + pattern + '\'' +
//                ", remark='" + remark + '\'' +
//                ", roleList=" + roleList +
//                '}';
//    }
}
