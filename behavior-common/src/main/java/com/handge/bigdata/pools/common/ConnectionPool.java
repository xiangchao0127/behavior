/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.handge.bigdata.pools.common;

import java.io.Serializable;

/**
 * Created by cloud computing on 2016/9/21 0021.
 */
public interface ConnectionPool<T> extends Serializable {


    public abstract T getConnection();

    public void returnConnection(T conn);


    public void invalidateConnection(T conn);
}
