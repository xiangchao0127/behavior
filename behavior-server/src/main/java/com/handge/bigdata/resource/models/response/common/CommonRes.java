package com.handge.bigdata.resource.models.response.common;

/**
 * @author Liujuhao
 * @date 2018/6/13.
 */
public class CommonRes {
    private String code;

    private String description;

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

    @Override
    public String toString() {
        return "CommonRes{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
