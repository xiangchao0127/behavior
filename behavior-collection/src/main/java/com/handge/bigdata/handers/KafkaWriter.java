/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.handge.bigdata.handers;

import com.handge.bigdata.handlerchain.BackendHandler;
import com.handge.bigdata.handlerchain.Context;
import com.handge.bigdata.pools.kafka.KafkaConnectionPool;
import com.handge.bigdata.proto.Behavior.BehaviorData;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.ArrayList;


public class KafkaWriter extends BackendHandler<Producer<byte[], byte[]>, KafkaConnectionPool> {

    private volatile String topic = null;
    private boolean isGC = false;

    public KafkaWriter(KafkaConnectionPool kafkaConnectionPool) {
        super(kafkaConnectionPool);
        topic = configuration.getString("behavior.topic", "behavior");
    }

    @Override
    public Object handle(Producer client, Context context) {
        try {
            ArrayList<BehaviorData> behaviorDataArrayList = (ArrayList<BehaviorData>) context.getContext();
            for (BehaviorData p : behaviorDataArrayList) {
                client.send(new ProducerRecord(topic, p.toByteArray()));
            }
        } catch (Exception e) {
            exception(this.getClass().toString(), e);
        }
        return null;
    }
}
