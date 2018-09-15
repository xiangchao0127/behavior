package com.handge.bigdata.context;

import com.handge.bigdata.config.Configure;
import com.handge.bigdata.pools.EnvironmentContainer;
import org.apache.commons.configuration2.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Liujuhao
 * @date 2018/6/28.
 */


@Component
public class CustomProperty {

    public CustomProperty() {
        Configuration config = Configure.getInstance(true).getDBConfiguration();
        this.successDescription = config.getString("server.response.common.success.description");
        this.shiroPort = config.getInt("server.redis.shiro.port");
        this.shiroHost = config.getString("server.redis.shiro.host");
        this.shiroExpire = config.getInt("server.redis.shiro.expire");
        this.shiroSessionInMemoryTimeout = config.getInt("server.redis.shiro.sessionInMemoryTimeout");
        this.isHidingName = config.getBoolean("server.test.hiding.name");
        this.isValidateIP = config.getBoolean("server.auth.login.ip.validate");
        this.allowIPs = config.getList(String.class, "server.auth.login.ip.allow");
        this.serverPort = config.getInt("server.host.port");
        this.shiroPrincipalField = config.getString("server.redis.shiro.principal.field");
    }

    public String successDescription;
    public String shiroHost;
    public int shiroPort;
    public int shiroExpire;
    public int shiroSessionInMemoryTimeout;
    public boolean isHidingName;
    public boolean isValidateIP;
    public List<String> allowIPs;
    public int serverPort;
    public String shiroPrincipalField;

}
