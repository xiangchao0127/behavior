package com.handge.bigdata.dao.proxy;

import com.handge.bigdata.config.Configure;
import com.handge.bigdata.pools.EnvironmentContainer;
import com.handge.bigdata.pools.Pools;
import org.apache.commons.configuration2.Configuration;
import redis.clients.jedis.Jedis;

/**
 * Created by Liujuhao on 2018/4/28.
 */
public class RedisProxy implements Proxy {
    private Pools pools = null;

    public RedisProxy() {
        EnvironmentContainer.setENV();
        Configure configure = Configure.getInstance(true);
        Configuration cnf = configure.getDBConfiguration();
        pools = Pools.createPool(cnf);
    }

    @Override
    public Jedis getConnection() {
        return pools.getRedis();
    }

    @Override
    public void returnRedis(Jedis redis) {
        pools.returnRedis(redis);
    }


}
