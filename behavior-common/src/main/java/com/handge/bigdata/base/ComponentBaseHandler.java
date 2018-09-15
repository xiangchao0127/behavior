/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.handge.bigdata.base;

import com.handge.bigdata.config.Configure;
import com.handge.bigdata.pools.Pools;
import org.apache.commons.configuration2.Configuration;
import org.apache.kafka.clients.producer.Producer;
import org.elasticsearch.client.transport.TransportClient;
import redis.clients.jedis.Jedis;

import java.sql.Connection;
import java.util.concurrent.*;

public abstract class ComponentBaseHandler {

    public final Pools pools;

    public Configuration configuration;

    public ExecutorService executorService;

    public volatile boolean flag = true;

    public ComponentBaseHandler(Configuration configuration, ExecutorService executorService) {
        this.pools = Pools.createPool(configuration);
        this.configuration = configuration;
        this.executorService = executorService;
    }

    public ComponentBaseHandler(Configuration configuration) {
        this(configuration, Executors.newWorkStealingPool());
    }

    public ComponentBaseHandler() {
        this(Configure.getInstance(true).getConfiguration(), Executors.newWorkStealingPool());
    }

    public Pools getPools() {
        return pools;
    }


    public Configuration getConfiguration() {
        return configuration;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public boolean getFlag() {
        return flag;
    }


    public void destroy() {
        pools.destroyAll();
        configuration.clear();
        executorService.shutdown();
        flag = false;
    }

    public <V> Future<V> handle(Callable<V> task) {

        return this.executorService.submit(task);
    }

//    public <V> Future<V> allcall(AllCall<V> allCall){
//        allCall.setPools(pools);
//        Future<V> t = this.handle(allCall);
//
//        return t;
//
//    }


    public abstract class TaskCallable<V> implements Callable<V> {
        public V start(long timeout, TimeUnit timeUnit) {
            V v = null;
            try {
                if (timeout == 0 || timeUnit == null) {
                    v = handle(this).get();
                } else {
                    v = handle(this).get(timeout, timeUnit);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return v;
        }
    }

    /**
     * Wrap task to runnable.
     *
     * @param <V>
     */
    public abstract class WrapTask<V> extends TaskCallable<V> {

        //todo  do something with Pools . but remember return your connect by yourself
        public V run() {
            return run(0, null);
        }

        //todo  do something with Pools . but remember return your connect by yourself
        public V run(long timeout, TimeUnit timeUnit) {
            return start(timeout, timeUnit);
        }
    }

    /**
     * Wrap task to runnable.  internal use  {@link TransportClient}
     *
     * @param <V>
     */
    public abstract class WrapTaskUseEsTrspCli<V> extends TaskCallable<V> {

        public final TransportClient transportClient = pools.getEsConnection();

        public V run() {
            return run(0, null);
        }

        public V run(long timeout, TimeUnit timeUnit) {
            V v = start(timeout, timeUnit);
            pools.returnEsConnection(transportClient);
            return v;
        }
    }


    /**
     * Wrap task to runnable.  internal use  {@link TransportClient  } and {@link Connection}
     *
     * @param <V>
     */
    public abstract class WrapTaskUseEsTrspCliAndMysql<V> extends TaskCallable<V> {

        public final TransportClient transportClient = pools.getEsConnection();
        public final Connection mysqlConnection = pools.getMysqlConnection();

        public V run() {
            return run(0, null);
        }

        public V run(long timeout, TimeUnit timeUnit) {
            V v = start(timeout, timeUnit);
            pools.returnEsConnection(transportClient);
            pools.returnMysqlConnection(mysqlConnection);

            return v;
        }
    }

    /**
     * @param <V>
     */
    public abstract class WrapTaskUseEsTrspCliProxy<V> extends TaskCallable<V> {

        public V run() {
            return run(0, null);
        }

        public V run(long timeout, TimeUnit timeUnit) {
            V v = start(timeout, timeUnit);
            return v;
        }
    }

    public abstract class WrapReturnConnector<V> {

        public abstract void run();
    }

    /**
     * Wrap task to runnable.  internal use  {@link Connection}
     *
     * @param <V>
     */
    public abstract class WrapTaskUseMysql<V> extends TaskCallable<V> {
        public final Connection mysqlConnection = pools.getMysqlConnection();

        public V run() {
            return run(0, null);
        }

        public V run(long timeout, TimeUnit timeUnit) {
            V v = start(timeout, timeUnit);
            pools.returnMysqlConnection(mysqlConnection);
            return v;
        }
    }

    /**
     * Wrap task to runnable.  internal use  {@link Producer}
     *
     * @param <V>
     */
    public abstract class WrapTaskUseKafkaProducer<V> extends TaskCallable<V> {
        public final Producer producer = pools.getKafkaProducer();

        public V run() {
            return run(0, null);
        }

        public V run(long timeout, TimeUnit timeUnit) {
            V v = start(timeout, timeUnit);
            pools.returnKafkaProducer(producer);
            return v;
        }
    }

    public abstract class WrapTaskUseAllConnect<V> extends TaskCallable<V> {
        public final Producer producer = pools.getKafkaProducer();
        public final Connection mysqlConnection = pools.getMysqlConnection();
        public final TransportClient transportClient = pools.getEsConnection();
        public final org.apache.hadoop.hbase.client.Connection hbaseconnection = pools.getHbaseConnection();
        public final Jedis jedis = pools.getRedis();

        public V run() {
            return run(0, null);
        }

        public V run(long timeout, TimeUnit timeUnit) {
            V v = start(timeout, timeUnit);
            pools.returnKafkaProducer(producer);
            pools.returnMysqlConnection(mysqlConnection);
            pools.returnEsConnection(transportClient);
            pools.returnHbaseConnection(hbaseconnection);
            pools.returnRedis(jedis);
            return v;
        }


    }


}
