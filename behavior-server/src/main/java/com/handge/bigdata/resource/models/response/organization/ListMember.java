package com.handge.bigdata.resource.models.response.organization;

import com.handge.bigdata.resource.models.request.base.PageParam;

import java.io.Serializable;

/**
 * @author liuqian
 * @date 2018/6/14
 * @Description:
 */
public class ListMember extends PageParam implements Serializable {
    /**
     * 姓名
     */
    private String name;

    /**
     * 工号
     */
    private String number;

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

    @Override
    public String toString() {
        return "ListMemberParam{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                '}';
    }
}
