package com.handge.bigdata.resource.models.request.statistics;

import com.handge.bigdata.resource.models.request.base.TimeParam;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/***
 * 上网足迹查询参数封装
 * @author MaJianfu
 * @date 2018/4/25 10:11
 * @param
 * @return
 **/
public class FootPrintParam extends TimeParam implements Serializable {
    /**
     * 用户
     */
    @NotNull(message = "用户名")
    private String name;
    /**
     * IP地址
     */
    private String ip;
    /**
     * 应用
     */
    private String app;
    /**
     * 类型
     */
    private String type;
    /**
     * 性质
     */
    private String property;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }


    @Override
    public String toString() {
        return "FootPrintParam{" +
                "name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", app='" + app + '\'' +
                ", type='" + type + '\'' +
                ", property='" + property + '\'' +
                '}';
    }
}
