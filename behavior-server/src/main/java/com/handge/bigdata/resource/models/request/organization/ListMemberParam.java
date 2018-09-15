package com.handge.bigdata.resource.models.request.organization;

/**
 * @author liuqian
 * @date 2018/6/20
 * @Description:
 */
public class ListMemberParam {
    /**
     * 部门id
     */
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ListMemberParam{" +
                "id='" + id + '\'' +
                '}';
    }
}
