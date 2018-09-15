package com.handge.bigdata.resource.models.request.base;

import com.handge.bigdata.resource.models.UserContext;

import java.io.Serializable;

/**
 * Created by DaLu Guo on 2018/5/31.
 */
public class UserContextParam implements Serializable {
    private UserContext userContext;

    public UserContext getUserContext() {
        return userContext;
    }

    public void setUserContext(UserContext userContext) {
        this.userContext = userContext;
    }

    @Override
    public String toString() {
        return "UserContextParam{" +
                "userContext=" + userContext +
                '}';
    }
}
