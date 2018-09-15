package com.handge.bigdata.resource.models.response.base;

/**
 * 存储部门信息实体
 *
 * @param
 * @author XiangChao
 * @date 2018/5/7 14:27
 * @return
 **/
public class DepartmentInfo {
    /**
     * 部门ID，主键唯一编号
     */
    private String departmentId;
    /**
     * 部门名称
     */
    private String departmentName;
    /**
     * 部门负责人
     */
    private String departmentHeader;
    /**
     * 创建时间
     */
    private String createAt;

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
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

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    @Override
    public String toString() {
        return "DepartmentInfo{" +
                "departmentId='" + departmentId + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", departmentHeader='" + departmentHeader + '\'' +
                ", createAt='" + createAt + '\'' +
                '}';
    }
}
