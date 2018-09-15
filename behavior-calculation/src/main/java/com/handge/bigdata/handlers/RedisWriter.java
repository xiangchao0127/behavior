/*
 * Copyright (c) 2018  Inc. All rights reserved
 * Projects : net-behavior
 * Module : behavior-calculation
 * Class : RedisWriter
 * User : XueFei Wang
 * Date : 5/30/18 2:48 PM
 * Modified :5/30/18 2:48 PM
 * Todo :
 *
 */

package com.handge.bigdata.handlers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.handge.bigdata.enumeration.DateFormatEnum;
import com.handge.bigdata.handlerchain.BackendHandler;
import com.handge.bigdata.handlerchain.Context;
import com.handge.bigdata.pools.jdbc.JdbcConnectionPool;
import com.handge.bigdata.proto.Behavior.BehaviorData;
import com.handge.bigdata.utils.DateUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class RedisWriter extends BackendHandler<Connection, JdbcConnectionPool> {

    /**
     * 查询工作无关标签
     */
    private static final String tagSql = "SELECT property FROM tag_property WHERE tag_name = '%s'";
    /**
     * 查询数据库对应ip
     */
    private static final String ipSql = "SELECT\n" +
            "\tdev.static_ip,\n" +
            "\temp.number\n" +
            "FROM\n" +
            "entity_employee_information_basic emp\n" +
            "JOIN auth_account cou ON cou.employee_id = emp.id\n" +
            "JOIN entity_device_basic dev ON dev.account_id = cou.id\n" +
            "WHERE dev.static_ip = '%s'";
    static Cache<String, String> map = CacheBuilder.newBuilder().concurrencyLevel(10).expireAfterWrite(1, TimeUnit.HOURS).build();
    private JedisPool jedisPool = null;

    public RedisWriter(JdbcConnectionPool jdbcConnectionPool, JedisPool jedisPool) {
        super(jdbcConnectionPool);
        this.jedisPool = jedisPool;
    }


    @Override
    public Object handle(Connection client, Context context) {
        Jedis jedis = jedisPool.getResource();
        try {
            ArrayList<BehaviorData> behaviorDatas = (ArrayList<BehaviorData>) context.getContext();
            for (BehaviorData data : behaviorDatas) {
               if( data.getIsnoise()){
                   continue;
               }
                if (isEmployeeIp(client, data)) {
                    if (isNonWorkingTag(client, data)) {
                        behaviorDataToRedis(jedis, data);
                    }
                }
            }
        } catch (Exception e) {
            exception(this.getClass().getName(), e);
        } finally {
            jedisPool.returnResource(jedis);
        }

        return false;
    }

    //判断是否为员工IP
    private boolean isEmployeeIp(Connection client, BehaviorData behaviorData) throws ExecutionException {
        String isEmployeeIP = map.get(behaviorData.getLocalIp(), new Callable<String>() {
            @Override
            public String call() throws Exception {
                //标志是否为员工IP  0 : false
                String mark = "0";
                PreparedStatement preparedStatement = null;
                ResultSet resultSet = null;
                try {
                     preparedStatement = client.prepareStatement(String.format(ipSql, behaviorData.getLocalIp()));
                     resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        mark = "1";
                    }
                }catch (Exception e){
                    throw new RuntimeException(e);
                }finally {
                    if (preparedStatement != null) {
                        preparedStatement.close();
                    }
                    if (resultSet != null){
                        resultSet.close();
                    }
                }

                return mark;
            }
        });
        if ("1".equals(isEmployeeIP)) {
            return true;
        }
        return false;
    }

    //判断是否为工作无关标签
    private boolean isNonWorkingTag(Connection client, BehaviorData behaviorData) throws ExecutionException {
        List<String> appTagsList = behaviorData.getAppTagsList();
        for (String tag : appTagsList) {
            String mark = map.get(tag, new Callable<String>() {
                @Override
                public String call() throws Exception {
                    PreparedStatement preparedStatement = null;
                    ResultSet resultSet = null ;
                    String result = null;
                    try {
                        preparedStatement = client.prepareStatement(String.format(tagSql, tag));
                        resultSet = preparedStatement.executeQuery();
                        while (resultSet.next()) {
                            result =  resultSet.getString("property");
                        }
                    }catch (Exception e){
                        throw  new RuntimeException(e);
                    }finally {
                        if (preparedStatement != null){
                            preparedStatement.close();
                        }
                        if (resultSet != null){
                            resultSet.close();
                        }
                    }
                    return result;
                }
            });
            if ("0".equals(mark)) {
                return true;
            }
        }
        return false;
    }

    //插入redis
    private void behaviorDataToRedis(Jedis jedis, BehaviorData data) throws ParseException {
        String dateKey = DateUtil.date2Str(new Date(), DateFormatEnum.DAY);
        if (!jedis.exists(dateKey)) {
            jedis.hset(dateKey, data.getLocalIp(), "1");
        } else {
            jedis.hincrBy(dateKey, data.getLocalIp(), 1);
        }

        jedis.expireAt(dateKey, Long.valueOf(String.valueOf(DateUtil.dateToStartOrEndStamp(dateKey + " 00:00:00", 1)).substring(0, 10)));

    }


}
