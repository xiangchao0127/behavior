package com.handge.bigdata.enumeration;


import com.handge.bigdata.dao.proxy.EsProxy;
import com.handge.bigdata.dao.proxy.MySQLProxy;
import com.handge.bigdata.dao.proxy.RedisProxy;

/**
 * Created by Liujuhao on 2018/4/28.
 */
public enum DAOProxyEnum {

    ES(EsProxy.class),
    MySQL(MySQLProxy.class),
    Redis(RedisProxy.class);

    private Class clazz;

    DAOProxyEnum(Class clazz) {
        this.clazz = clazz;
    }

    public Class getClazz() {
        return clazz;
    }
}
