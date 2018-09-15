package com.handge.bigdata.dao.proxy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import redis.clients.jedis.Jedis;

/**
 * Created by Liujuhao on 2018/4/28.
 */
public interface Proxy {

    Log logger = LogFactory.getLog(Proxy.class);

    default public Object queryBySQL(String sql) {
        logger.debug("Execute SQL：" + sql + "\n" + "But,代理没有实现该方法-_-");
        return null;
    }

    default public Object getConnection() {
        logger.debug("获取连接");
        return null;
    }

    default public Object getClient() {
        logger.debug("获取客户端");
        return null;
    }

    default Object action(Object srb) {
        logger.debug("立刻执行查询操作" + "\n" + "But,代理没有实现该方法-_-");
        return null;
    }

    default void returnClient() {
        logger.debug("立即归还连接" + "\n" + "But,代理没有实现该方法-_-");
    }

    default void returnRedis(Jedis redis) {
        logger.debug("立即归还redis连接");
    }
}
