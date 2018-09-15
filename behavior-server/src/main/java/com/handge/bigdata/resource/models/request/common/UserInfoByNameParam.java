package com.handge.bigdata.resource.models.request.common;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * Created by DaLu Guo on 2018/6/5.
 */
public class UserInfoByNameParam implements Serializable {
    @NotEmpty(message = "用户名/部门/工号")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String user) {
        this.name = user;
    }

    @Override
    public String toString() {
        return "UserInfoByNameParam{" +
                "name='" + name + '\'' +
                '}';
    }
}
