package com.handge.bigdata.resource.models.response.common;

/**
 * @author Liujuhao
 * @date 2018/6/11.
 */
public class LoginRes {

    private String token;

    private String code;

    private String description;

    private UserInfo userInfo;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String toString() {
        return "LoginRes{" +
                "token='" + token + '\'' +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", userInfo=" + userInfo +
                '}';
    }
}
