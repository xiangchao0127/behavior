package com.handge.bigdata.resource.models.response.professional;

import java.util.List;

/**
 * Created by DaLu Guo on 2018/6/13.
 */
public class EmployeeDetailsInfo {
    /**
     * 标签集合
     */
    private List<String> tags;
    /**
     * 姓名
     */
    private String name;
    /**
     * 工号
     */
    private String number;
    /**
     * 部门
     */
    private String department;
    /**
     * 职位
     */
    private String post;
    /**
     * 工龄
     */
    private String seniority;
    /**
     * 岗龄
     */
    private String postAge;
    /**
     * 职称
     */
    private String positionalTitles;
    /**
     * 状态
     */
    private String status;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getSeniority() {
        return seniority;
    }

    public void setSeniority(String seniority) {
        this.seniority = seniority;
    }

    public String getPostAge() {
        return postAge;
    }

    public void setPostAge(String postAge) {
        this.postAge = postAge;
    }

    public String getPositionalTitles() {
        return positionalTitles;
    }

    public void setPositionalTitles(String positionalTitles) {
        this.positionalTitles = positionalTitles;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "EmployeeDetailsInfo{" +
                "tags=" + tags +
                ", name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", department='" + department + '\'' +
                ", post='" + post + '\'' +
                ", seniority='" + seniority + '\'' +
                ", postAge='" + postAge + '\'' +
                ", positionalTitles='" + positionalTitles + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
