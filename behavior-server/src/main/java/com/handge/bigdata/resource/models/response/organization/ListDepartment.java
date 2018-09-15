package com.handge.bigdata.resource.models.response.organization;

/**
 * @author liuqian
 * @date 2018/6/20
 * @Description:
 */
public class ListDepartment {
    /**
     * 部门id
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

    @Override
    public String toString() {
        return "ListDepartment{" +
                "id='" + id + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", departmentHeader='" + departmentHeader + '\'' +
                ", higherDepartment='" + higherDepartment + '\'' +
                ", departmentDesc='" + departmentDesc + '\'' +
                '}';
    }
}
