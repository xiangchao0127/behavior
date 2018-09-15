package com.handge.bigdata.resource.models.request.organization;

import com.handge.bigdata.resource.models.request.base.PageParam;

import java.io.Serializable;

/**
 * @author liuqian
 * @date 2018/6/21
 * @Description:
 */
public class FindNameAndNumberParam extends PageParam implements Serializable {
    /**
     * 员工姓名
     */
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "FindNameAndNumberParam{" +
                "name='" + name + '\'' +
                '}';
    }
}
