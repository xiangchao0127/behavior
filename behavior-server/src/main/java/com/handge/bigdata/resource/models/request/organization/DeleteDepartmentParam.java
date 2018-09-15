package com.handge.bigdata.resource.models.request.organization;

import java.util.List;

/**
 * @author liuqian
 * @date 2018/6/20
 * @Description:
 */
public class DeleteDepartmentParam {
    /**
     * 部门id集合
     */
    private List<String> ids;

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    @Override
    public String toString() {
        return "DeleteDepartmentParam{" +
                "ids=" + ids +
                '}';
    }
}
