/*
 * Copyright (c) 2018  Inc. All rights reserved
 * Projects : net-behavior
 * Module : behavior-common
 * Class : BaseHandler
 * User : XueFei Wang
 * Date : 5/24/18 11:08 AM
 * Modified :5/22/18 1:31 PM
 * Todo :
 *
 */

package com.handge.bigdata.base;

import com.handge.bigdata.config.Configure;
import com.handge.bigdata.pools.common.AbstractPool;
import org.apache.commons.configuration2.Configuration;

import java.util.concurrent.*;

public abstract class BaseHandler<T, P extends AbstractPool<T>> {

    public P pool;

    public Configuration configuration;

    public ExecutorService executorService;

    public volatile boolean flag = true;

    public BaseHandler(P pool, Configuration configuration, ExecutorService executorService) {
        this.pool = pool;
        this.configuration = configuration;
        this.executorService = executorService;
    }

    public BaseHandler(P pool, Configuration configuration) {
        this(pool, configuration, Executors.newWorkStealingPool());
    }

    public BaseHandler(P pool) {
        this(pool, Configure.getInstance(true).getConfiguration());
    }


    public AbstractPool<T> getPool() {
        return pool;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public boolean isFlag() {
        return flag;
    }


    public void setConnectionPools(P pool) {
        this.pool = pool;
    }


    public void setThreadExcutors(ExecutorService executorService) {
        this.executorService = executorService;
    }


    public void destroy() {
        pool.close();
        configuration.clear();
        executorService.shutdown();
        flag = false;
    }

    public <V> Future<V> handle(Callable<V> task) {

        return this.executorService.submit(task);
    }


    public abstract class WrapTask<V> implements Callable<V> {
        public final T clent = pool.getConnection();

        public V run() {
            return run(0, null);
        }

        public V run(long timeout, TimeUnit timeUnit) {
            V v = null;
            try {
                if (timeout == 0 || timeUnit == null) {
                    v = handle(this).get();
                } else {
                    v = handle(this).get(timeout, timeUnit);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                pool.returnConnection(clent);
            }
            return v;
        }
    }
}
