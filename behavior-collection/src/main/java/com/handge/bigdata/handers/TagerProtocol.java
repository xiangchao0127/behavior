/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.handge.bigdata.handers;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.handge.bigdata.base.Tuple2;
import com.handge.bigdata.handlerchain.BackendHandler;
import com.handge.bigdata.handlerchain.Context;
import com.handge.bigdata.pools.jdbc.JdbcConnectionPool;
import com.handge.bigdata.proto.Behavior.BehaviorData;

import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class TagerProtocol extends BackendHandler<Connection, JdbcConnectionPool> {


    static Cache<String, Tuple2<String, String>> protocol2tags = CacheBuilder.newBuilder().concurrencyLevel(10).maximumSize(1000).expireAfterWrite(1, TimeUnit.MILLISECONDS).build();


    static String protocolTagStatement = "select tag , appname from  tag_protocol where protocol = '%s'";

    static String addProtocolStatement = "insert into tag_protocol (protocol) values( '%s')";

    public TagerProtocol(JdbcConnectionPool pool) {
        super(pool);
    }

    @Override
    public Object handle(Connection client, Context context) {

            ArrayList<BehaviorData> behaviorDatas = (ArrayList<BehaviorData>) context.getContext();
            ArrayList<BehaviorData> behaviorDataArrayList = new ArrayList<BehaviorData>(behaviorDatas.size());
            for (BehaviorData data : behaviorDatas) {
                BehaviorData.Builder builder = data.toBuilder();
                builder.setIsnoise((noise(data) || isDNS(data)));
                try {
                Tuple2<String, String> t_a = getTagByProtocol(builder.getAppProtocol(), client);
                if (t_a != null) {
                    if (t_a.a != null) builder.addAppTags(t_a.a);
                    if (t_a.b != null) builder.setAppName(t_a.b);
                }} catch (ExecutionException e) {
                    exception(this.getClass().getName(), e);
                }
                behaviorDataArrayList.add(builder.build());
            }
            context.setContext(behaviorDataArrayList);

        return false;
    }


    public Tuple2<String, String> getTagByProtocol(String protocol, Connection client) throws ExecutionException {
        Tuple2<String, String> tag_appname = protocol2tags.get(protocol, new Callable<Tuple2<String, String>>() {
            @Override
            public Tuple2<String, String> call() throws SQLException {
                String tagTemp = null;
                String appName = null;
                boolean flag = true;
                PreparedStatement prepareStatament = null;
                ResultSet resultSet = null;
                Statement createStatement = null;
                try {
                    prepareStatament = client.prepareStatement(String.format(protocolTagStatement, protocol));
                    resultSet = prepareStatament.executeQuery();
                    createStatement = client.createStatement();

                    while (resultSet.next()) {
                        tagTemp = resultSet.getString("tag");
                        appName = resultSet.getString("appname");
                        flag = false;
                    }

                    if (flag) {

                        boolean isok = createStatement.execute(String.format(addProtocolStatement, protocol));

                        if (isok) System.out.println("========> insert to proto2tag with protocol = " + protocol);
                    }
                    if (appName == null) {
                        appName = protocol;
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }finally {
                  if(resultSet != null)  resultSet.close();
                  if (prepareStatament!=null)   prepareStatament.close();
                  if (createStatement != null)  createStatement.close();
                }

                return new Tuple2<String, String>(tagTemp, appName);
            }
        });
        return tag_appname;
    }


    /**
     * if noise  return true
     *
     * @param behaviorData
     * @return
     */
    public boolean noise(BehaviorData behaviorData) {
        int flag = 0;
        if (behaviorData.getAppProtocol().equalsIgnoreCase("Unknown") || behaviorData.getAppProtocol().isEmpty()) {
            flag = flag + 1;
        }
        if (behaviorData.getHost() == null || behaviorData.getHost().trim().isEmpty()) {
            flag = flag + 1;
        }
        if (behaviorData.getServer().isEmpty() && behaviorData.getClient().isEmpty()) {
            flag = flag + 1;
        }

        return (flag > 1 ? true : false);
    }


    public boolean isDNS(BehaviorData behaviorData) {
        if (behaviorData.getAppProtocol().startsWith("DNS")) {
            return true;
        }
        if (behaviorData.getHttpUrl().startsWith(".jpg")) {
            return true;
        }
        if (behaviorData.getHttpUrl().startsWith(".png")) {
            return true;
        }
        if (behaviorData.getHttpUrl().startsWith(".css")) {
            return true;
        }
        if (behaviorData.getHttpUrl().startsWith(".js")) {
            return true;
        }
        if (behaviorData.getHttpUrl().startsWith(".mp4")) {
            return true;
        }
        return false;
    }

}
