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
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.ArrayList;

public class KafkaProducerBase extends KafkaProducerBaseOperation {

    public KafkaProducerBase(KafkaConnectionPool pool) {
        super(pool);
    }

    @Override
    public Object sendMsg(Object... objects) {
        Object result = new WrapTask() {
            @Override
            public Object call() throws Exception {
                Producer producer = (Producer) this.clent;
                ArrayList list = new ArrayList();
                for (Object object : objects) {
                    if (object instanceof ProducerRecord) {
                        ProducerRecord record = (ProducerRecord) object;
                        Object rs = producer.send(record).get();
                        list.add(rs);
                    }
                }
                return list;
            }
        }.run();
        return objects;
    }

}
