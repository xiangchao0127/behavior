/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.handge.bigdata.base;

import com.handge.bigdata.pools.kafka.KafkaConnectionPool;
import org.apache.kafka.clients.producer.Producer;

public abstract class KafkaProducerBaseOperation extends BaseHandler<Producer<byte[], byte[]>, KafkaConnectionPool> implements DataOperation {
    public KafkaProducerBaseOperation(KafkaConnectionPool pool) {
        super(pool);
    }


    public abstract Object sendMsg(Object... objects);


    // TODO: don`t do anything
    @Override
    public Object delete(Object... objects) {
        return null;
    }

    // TODO: don`t do anything
    @Override
    public Object update(Object... objects) {
        return null;
    }

    // TODO: don`t do anything
    @Override
    public Object searche(Object... objects) {
        return null;
    }

    @Override
    public Object create(Object... objects) {
        return sendMsg(objects);
    }

    // TODO: don`t do anything
    @Override
    public Object get(Object... objects) {
        return null;
    }
}