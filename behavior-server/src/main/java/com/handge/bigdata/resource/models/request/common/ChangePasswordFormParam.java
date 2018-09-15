package com.handge.bigdata.resource.models.request.common;

import javax.validation.constraints.NotEmpty;

/**
 * @author Liujuhao
 * @date 2018/6/13.
 */
public class ChangePasswordFormParam {

    @NotEmpty
    private String username;

    @NotEmpty
    private String oldPassword;

    @NotEmpty
    private String newPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public String toString() {
        return "ChangePasswordFormParam{" +
                "username='" + username + '\'' +
                ", oldPassword='" + oldPassword + '\'' +
                ", newPassword='" + newPassword + '\'' +
                '}';
    }
}
