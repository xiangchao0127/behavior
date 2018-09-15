///*
// * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
// * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
// * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
// * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
// * Vestibulum commodo. Ut rhoncus gravida arcu.
// */
//
//package com.handge.bigdata;
//
//import com.handge.bigdata.config.Configure;
//import com.handge.bigdata.pools.EnvironmentContainer;
//import com.handge.bigdata.pools.Pools;
//import com.handge.bigdata.handers.KafkaWriter;
//import org.apache.commons.chain.impl.ChainBase;
//import org.apache.commons.chain.impl.ContextBase;
//import org.apache.commons.configuration2.Configuration;
//
//public class WriterChain {
//    public static void main(String[] args) {
//        EnvironmentContainer.setENV();
//        Configuration config = Configure.getInstance(true).getDBConfiguration();
//        Pools pools = Pools.createPool(config);
//        ChainBase writerChain = new ChainBase();
//        writerChain.addCommand(new KafkaWriter(pools.getKafkaConnectionPool()));
//        try {
//            writerChain.execute(new ContextBase());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
