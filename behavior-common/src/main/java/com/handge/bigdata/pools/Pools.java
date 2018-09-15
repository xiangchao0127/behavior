package com.handge.bigdata.pools;

import com.handge.bigdata.pools.common.DefualtPool;
import com.handge.bigdata.pools.common.InternalPools;
import com.handge.bigdata.pools.common.PoolConfig;
import com.handge.bigdata.pools.es.EsConnectionPool;
import com.handge.bigdata.pools.hbase.HbaseConnectionPool;
import com.handge.bigdata.pools.jdbc.JdbcConnectionPool;
import com.handge.bigdata.pools.kafka.KafkaConnectionPool;
import org.apache.commons.configuration2.Configuration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.kafka.clients.producer.Producer;
import org.elasticsearch.client.transport.TransportClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;

/**
 * Created by xuefei_wang on 17-3-24.
 */
abstract public class Pools {

    public static volatile Pools pools = null;
    protected static volatile HbaseConnectionPool hbaseConnectionPool;

    protected static volatile EsConnectionPool esConnectionPool;

    protected static volatile JdbcConnectionPool jdbcConnectionPool;

    protected static volatile KafkaConnectionPool kafkaConnectionPool;

    protected static volatile JedisPool jedisPool;

    public PoolConfig poolConfig;

    public Pools(PoolConfig poolConfig) {
        this.poolConfig = poolConfig;

    }


    public Pools() {

    }

    @Deprecated
    public static Pools createPool(PoolConfig poolConfig, Map<String, String> paramters) {

        if (pools == null) {
            synchronized (Pools.class) {
                if (null == pools) {
                    pools = new InternalPools(poolConfig, paramters);
                }
            }
        }
        return pools;
    }


    public static Pools createPool(PoolConfig poolConfig, Configuration configuration) {

        if (pools == null) {
            synchronized (Pools.class) {
                if (null == pools) {
                    pools = new DefualtPool(poolConfig, configuration);
                }
            }
        }
        return pools;
    }

    public static Pools createPool(Configuration configuration) {
        return createPool(null, configuration);
    }

    public PoolConfig getPoolConfig() {
        if (poolConfig == null) {
            poolConfig = new PoolConfig();
            poolConfig.setMaxTotal(10);
            poolConfig.setMaxIdle(10);
            poolConfig.setMaxWaitMillis(1000);
            poolConfig.setTestOnBorrow(true);
            poolConfig.setTestOnReturn(true);
            poolConfig.setTestOnCreate(true);
        }
        return poolConfig;
    }

    public synchronized Connection getHbaseConnection() {

        if (hbaseConnectionPool == null || hbaseConnectionPool.isClosed()) {
            hbaseConnectionPool = getHbaseConnectionPool();
        }

        return hbaseConnectionPool.getConnection();
    }

    public synchronized void returnHbaseConnection(Connection connection) {

        hbaseConnectionPool.returnConnection(connection);
    }

    public synchronized TransportClient getEsConnection() {

        if (esConnectionPool == null || esConnectionPool.isClosed()) {
            esConnectionPool = getEsConnectionPool();
        }
        return esConnectionPool.getConnection();

    }

    public synchronized void returnEsConnection(TransportClient connection) {

        esConnectionPool.returnConnection(connection);

    }


    public synchronized java.sql.Connection getMysqlConnection() {

        if (jdbcConnectionPool == null || jdbcConnectionPool.isClosed()) {
            jdbcConnectionPool = getJdbcConnectionPool();
        }
        return jdbcConnectionPool.getConnection();

    }

    public synchronized void returnMysqlConnection(java.sql.Connection connection) {

        jdbcConnectionPool.returnConnection(connection);

    }


    public synchronized Producer getKafkaProducer() {

        if (kafkaConnectionPool == null || kafkaConnectionPool.isClosed()) {
            kafkaConnectionPool = getKafkaConnectionPool();
        }
        return kafkaConnectionPool.getConnection();

    }

    public synchronized void returnKafkaProducer(Producer producer) {

        kafkaConnectionPool.returnConnection(producer);

    }


    public synchronized Jedis getRedis() {

        if (jedisPool == null || jedisPool.isClosed()) {
            jedisPool = getRedisPool();
        }
        return jedisPool.getResource();

    }

    public synchronized void returnRedis(Jedis client) {
        jedisPool.returnResource(client);
    }


    public void destroyAll() {
        if (hbaseConnectionPool != null) {
            hbaseConnectionPool.close();
        }
        if (esConnectionPool != null) {
            esConnectionPool.close();
        }
        if (jdbcConnectionPool != null) {
            jdbcConnectionPool.close();
        }
        if (kafkaConnectionPool != null) {
            kafkaConnectionPool.close();
        }
        if (jedisPool != null) {
            jedisPool.close();
        }
    }

    abstract public HbaseConnectionPool getHbaseConnectionPool();

    abstract public EsConnectionPool getEsConnectionPool();

    abstract public JdbcConnectionPool getJdbcConnectionPool();

    abstract public KafkaConnectionPool getKafkaConnectionPool();

    abstract public JedisPool getRedisPool();
}
