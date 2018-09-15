package com.handge.bigdata.resource.models.response.organization;


import java.util.List;
import java.util.Map;

/**
 * @author liuqian
 * @date 2018/6/25
 * @Description:
 */
public class MoveEmployee {

    /**
     * 调出部门id及成员id集合
     */
    private Map<String,List<String>> outDepartmentMembers;

    /**
     * 调入部门id
     */
    private String inDepartmentId;

    public Map<String, List<String>> getOutDepartmentMembers() {
        return outDepartmentMembers;
    }

    public void setOutDepartmentMembers(Map<String, List<String>> outDepartmentMembers) {
        this.outDepartmentMembers = outDepartmentMembers;
    }

    public String getInDepartmentId() {
        return inDepartmentId;
    }

    public void setInDepartmentId(String inDepartmentId) {
        this.inDepartmentId = inDepartmentId;
    }

    @Override
    public String toString() {
        return "MoveEmployee{" +
                "outDepartmentMembers=" + outDepartmentMembers +
                ", inDepartmentId='" + inDepartmentId + '\'' +
                '}';
    }
}
