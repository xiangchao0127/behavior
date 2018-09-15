/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.handge.bigdata.datastore;


import com.handge.bigdata.pools.common.ConnectionPool;
import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.EnvironmentConfiguration;

import java.util.Map;
import java.util.Properties;

public abstract class ProxyAbstract<T extends ConnectionPool> {

    public final CompositeConfiguration config = new CompositeConfiguration();


    public T connectionPool = null;


    public ProxyAbstract() {
        EnvironmentConfiguration environmentConfiguration = new EnvironmentConfiguration();
        config.addConfiguration(environmentConfiguration);
    }

    public void addConfig(Properties properties) {


    }

    public void addConfig(Map map) {

    }

    public void addConfigFromMysql(String userName, String passWD, String database) {

    }


    abstract T initConnectionPool();

}
