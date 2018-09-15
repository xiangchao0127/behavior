package com.handge.bigdata.resource.models.request.common;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @author Liujuhao
 * @date 2018/6/11.
 */
public class LoginFormParam implements Serializable {

    @NotEmpty
    private String username;

    @NotEmpty
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginFormParam{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
