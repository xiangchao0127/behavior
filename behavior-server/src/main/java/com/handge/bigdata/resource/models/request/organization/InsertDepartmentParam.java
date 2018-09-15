package com.handge.bigdata.resource.models.request.organization;

import com.handge.bigdata.resource.models.request.base.PageParam;

import java.io.Serializable;

/**
 * @author liuqian
 * @date 2018/6/14
 * @Description:
 */
public class InsertDepartmentParam extends PageParam implements Serializable {

    /**
     * id
     */
    private String id;

    /**
     * 部门名称
     */
    private String departmentName;

    /**
     * 部门负责人
     */
    private String departmentHeader;

    /**
     * 上级部门
     */
    private String higherDepartment;

    /**
     * 职能描述
     */
    private String departmentDesc;

    /**
     * 创建时间
     */
    private String createDateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentHeader() {
        return departmentHeader;
    }

    public void setDepartmentHeader(String departmentHeader) {
        this.departmentHeader = departmentHeader;
    }

    public String getHigherDepartment() {
        return higherDepartment;
    }

    public void setHigherDepartment(String higherDepartment) {
        this.higherDepartment = higherDepartment;
    }

    public String getDepartmentDesc() {
        return departmentDesc;
    }

    public void setDepartmentDesc(String departmentDesc) {
        this.departmentDesc = departmentDesc;
    }

    public String getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(String createDateTime) {
        this.createDateTime = createDateTime;
    }

    @Override
    public String toString() {
        return "InsertDepartmentParam{" +
                "id='" + id + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", departmentHeader='" + departmentHeader + '\'' +
                ", higherDepartment='" + higherDepartment + '\'' +
                ", departmentDesc='" + departmentDesc + '\'' +
                ", createDateTime='" + createDateTime + '\'' +
                '}';
    }
}
