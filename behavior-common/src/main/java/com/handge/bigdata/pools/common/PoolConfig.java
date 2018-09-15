/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.handge.bigdata.pools.common;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.io.Serializable;


/**
 * Created by cloud computing on 2016/9/21 0021.
 */
public class PoolConfig extends GenericObjectPoolConfig implements Serializable {


    public static final boolean DEFAULT_TEST_WHILE_IDLE = true;

    public static final long DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS = 60000;

    public static final long DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS = 30000;

    public static final int DEFAULT_NUM_TESTS_PER_EVICTION_RUN = -1;

    private static final long serialVersionUID = -2414567557372345057L;


    public PoolConfig() {

        // defaults to make your life with connection pool easier :)
        setTestWhileIdle(DEFAULT_TEST_WHILE_IDLE);
        setMinEvictableIdleTimeMillis(DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS);
        setTimeBetweenEvictionRunsMillis(DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS);
        setNumTestsPerEvictionRun(DEFAULT_NUM_TESTS_PER_EVICTION_RUN);
    }


}
