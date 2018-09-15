/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.handge.bigdata;

import com.handge.bigdata.config.Configure;
import com.handge.bigdata.pools.EnvironmentContainer;
import com.handge.bigdata.pools.Pools;
import com.handge.bigdata.tree.TreeNode;
import com.handge.bigdata.tree.multinode.ArrayMultiTreeNode;
import org.apache.commons.configuration2.Configuration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.kafka.clients.producer.Producer;
import org.elasticsearch.client.transport.TransportClient;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/***
 * #!/usr/bin/env bash


 # config table schema :  (id , _key ,_value , desc)


 # mysql database user (required)
 export CONF_DB_USER=root

 # mysql password for user (required)
 export CONF_DB_PASSWORD=admin

 # mysql databses host ,port , database (if not set, CONF_DB_URL instead)
 export CONF_DB_HOST=172.20.31.108
 export CONF_DB_PORT=3306
 export CONF_DB_DATABASE=information

 # mysql database jdbcurl( if not set ,(CONF_DB_HOST, CONF_DB_PORT,CONF_DB_DATABASE) instead )

 # export CONF_DB_DATABASE = jdbc:mysql://host:port/database

 # mysql config table name (defaut config)
 export CONF_DB_TABLE=config




 example:

 CONF_DB_HOST=172.20.31.108
 CONF_DB_PORT=3306
 CONF_DB_DATABASE=information
 CONF_DB_TABLE=config
 CONF_DB_USER=root
 CONF_DB_PASSWORD=admin

 */
public class PoolsTest {

    public static void main(String[] args) throws IOException, SQLException {
        //  if not set env       run this
        EnvironmentContainer.setENV();
        Configure configure = Configure.getInstance(true);
        Configuration cnf = configure.getDBConfiguration();


        Pools pools = Pools.createPool(cnf);

        ExecutorService executors = Executors.newFixedThreadPool(20);


//        executors.submit(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    TransportClient esclient = pools.getEsConnection();
//                    System.out.println(esclient.admin().indices().prepareExists("msg5").execute().actionGet().isExists());
//                    pools.returnEsConnection(esclient);
//                }
//            }
//        });
//
//
//        executors.submit(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    Connection hclient = pools.getHbaseConnection();
//                    try {
//                        System.out.println(hclient.getAdmin().getClusterStatus());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    pools.returnHbaseConnection(hclient);
//                }
//            }
//        });


        executors.submit(new Runnable() {
            @Override
            public void run() {
                 final TreeNode<String> root = new ArrayMultiTreeNode<>("root");
                PreparedStatement prepareStatament = null;
                ResultSet resultSet = null;
                try {
                    prepareStatament = pools.getMysqlConnection().prepareStatement("select *  from lib_illegal_rules  where status = 1");
                    resultSet = prepareStatament.executeQuery();
                    while (resultSet.next()){
                        String proto = resultSet.getString("proto");
                        String domain = resultSet.getString("domain");
                        String keyWords = resultSet.getString("url_keyword");
                        String className = resultSet.getString("class");
                        String level = resultSet.getString("level");
                        String time_start = resultSet.getString("monitor_start_time");
                        String time_end = resultSet.getString("monitor_end_time");
                        String ipareas = resultSet.getString("monitor_range");
                        String type = resultSet.getString("type");



                            root.addAndGet(new ArrayMultiTreeNode<String>(proto))
                                    .addAndGet(new ArrayMultiTreeNode<String>(domain))
                                    .addAndGet(new ArrayMultiTreeNode<String>(keyWords))
                                    .addAndGet(new ArrayMultiTreeNode<String>(time_start + ","+time_end))
                                    .addAndGet(new ArrayMultiTreeNode<String>(ipareas))
                                    .addAndGet(new ArrayMultiTreeNode<String>(className))
                                    .addAndGet(new ArrayMultiTreeNode<String>(level))
                                    .addAndGet(new ArrayMultiTreeNode<String>(type));


                    }

                    System.out.println(root);
                }catch ( Exception e){
                    throw new RuntimeException(e);
                }finally {
                    if (prepareStatament != null) {
                        try {
                            prepareStatament.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    if (resultSet != null){
                        try {
                            resultSet.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

//        executors.submit(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    Producer producer = pools.getKafkaProducer();
//                    System.out.println(producer.partitionsFor("traffic"));
//                    pools.returnKafkaProducer(producer);
//                }
//            }
//        });
//
//        executors.submit(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    Jedis redis = pools.getRedis();
//                    System.out.println(redis.keys("*"));
//                    pools.returnRedis(redis);
//                }
//            }
//        });
    }


}
