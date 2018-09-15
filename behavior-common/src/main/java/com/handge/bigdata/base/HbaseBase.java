/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.handge.bigdata.base;

import com.handge.bigdata.pools.hbase.HbaseConnectionPool;
import org.apache.hadoop.hbase.client.Connection;

public class HbaseBase extends BaseHandler<Connection, HbaseConnectionPool> implements DataOperation {


    public HbaseBase(HbaseConnectionPool pool) {
        super(pool);
    }

    @Override
    public Object delete(Object... objects) {
        return null;
    }

    @Override
    public Object update(Object... objects) {
        return null;
    }

    @Override
    public Object searche(Object... objects) {
        return null;
    }

    @Override
    public Object create(Object... objects) {
        return null;
    }

    @Override
    public Object get(Object... objects) {
        return null;
    }
}
