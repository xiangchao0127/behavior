/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.handge.bigdata.pools.common;

import com.handge.bigdata.pools.Pools;
import com.handge.bigdata.pools.es.EsConnectionPool;
import com.handge.bigdata.pools.hbase.HbaseConnectionPool;
import com.handge.bigdata.pools.jdbc.JdbcConnectionPool;
import com.handge.bigdata.pools.kafka.KafkaConnectionPool;
import org.apache.commons.configuration2.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import redis.clients.jedis.JedisPool;

import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Properties;

public class DefualtPool extends Pools {

    public Configuration configuration;

    public DefualtPool(Configuration configuration) {
        this(null, configuration);
    }

    public DefualtPool(PoolConfig poolConfig, Configuration configuration) {
        super(poolConfig);
        this.configuration = configuration;
    }

    @Override
    public HbaseConnectionPool getHbaseConnectionPool() {
        org.apache.hadoop.conf.Configuration hconfig = HBaseConfiguration.create();
        hconfig.set("hbase.zookeeper.quorum", configuration.getString("hbase.zookeeper.quorum"));
        hconfig.set("hbase.zookeeper.property.clientPort", configuration.getString("hbase.zookeeper.clientport"));
        hconfig.set("zookeeper.znode.parent", configuration.getString("hbase.zookeeper.znodepearent"));
        return new HbaseConnectionPool(getPoolConfig(), hconfig);
    }

    @Override
    public EsConnectionPool getEsConnectionPool() {
        Settings.Builder settings = Settings.builder();
        settings.put("cluster.name", configuration.getString("elastic.cluster.name"));
        settings.put("transport.type", "netty3");
        settings.put("http.type", "netty3");
        LinkedList<InetSocketTransportAddress> address = new LinkedList<InetSocketTransportAddress>();
        String[] hosts = configuration.getString("elastic.cluster.url").split(",");
        for (String host : hosts) {
            String[] hp = host.split(":");
            try {
                address.add(new InetSocketTransportAddress(InetAddress.getByName(hp[0]), Integer.valueOf(hp[1])));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new EsConnectionPool(getPoolConfig(), settings.build(), address);
    }


    @Override
    public JdbcConnectionPool getJdbcConnectionPool() {
        String MYSQL_DRIVER = configuration.getString("mysql.driver");
        String MYSQL_JDBC_URL = configuration.getString("mysql.url");
        String MYSQL_USER_NAME = configuration.getString("mysql.user");
        String MYSQL_USER_PASSWORD = configuration.getString("mysql.password");
        JdbcConnectionPool mysqlPool = new JdbcConnectionPool(
                getPoolConfig(),
                MYSQL_DRIVER,
                MYSQL_JDBC_URL,
                MYSQL_USER_NAME,
                MYSQL_USER_PASSWORD
        );
        return mysqlPool;
    }

    @Override
    public KafkaConnectionPool getKafkaConnectionPool() {
        Properties kafkaConfig = new Properties();
        kafkaConfig.setProperty("bootstrap.servers", configuration.getString("kafka.bootstrap.servers"));
        kafkaConfig.setProperty("producer.type", configuration.getString("kafka.producer.type"));
        kafkaConfig.setProperty("key.serializer", configuration.getString("kafka.key.serializer"));
        kafkaConfig.setProperty("value.serializer", configuration.getString("kafka.value.serializer"));
        kafkaConfig.setProperty("batch.num.messages", configuration.getString("kafka.batch.num.messages", "20"));
        kafkaConfig.setProperty("max.request.size", configuration.getString("kafka.max.requestsize"));
        kafkaConfig.setProperty("enable.auto.commit", configuration.getString("kafka.enable.autocommit"));
        kafkaConfig.setProperty("auto.offset.reset", configuration.getString("kafka.auto.offsetreset"));
        KafkaConnectionPool kafkaPool = new KafkaConnectionPool(getPoolConfig(), kafkaConfig);
        return kafkaPool;
    }

    @Override
    public JedisPool getRedisPool() {
        String[] REDIS_HOST = configuration.getString("redis.cluster.url").split(":");

        JedisPool jedisPool = new JedisPool(
                getPoolConfig(),
                REDIS_HOST[0],
                Integer.valueOf(REDIS_HOST[1]));
        return jedisPool;
    }
}
