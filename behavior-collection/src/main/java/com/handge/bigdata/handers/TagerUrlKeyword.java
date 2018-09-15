/*
 * Copyright (c) 2018  Inc. All rights reserved
 * Projects : net-behavior
 * Module : behavior-collection
 * Class : TagerUrlKeyword
 * User : XueFei Wang
 * Date : 5/28/18 2:53 PM
 * Modified :5/28/18 11:36 AM
 * Todo :
 *
 */

package com.handge.bigdata.handers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.handge.bigdata.base.Tuple2;
import com.handge.bigdata.handlerchain.BackendHandler;
import com.handge.bigdata.handlerchain.Context;
import com.handge.bigdata.pools.jdbc.JdbcConnectionPool;
import com.handge.bigdata.proto.Behavior.BehaviorData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class TagerUrlKeyword extends BackendHandler<Connection, JdbcConnectionPool> {

    static final String sql_searchTag = "select app_name , basic_class from tag_url where app_domain_name = '%s'";
    static final String sql_insert = "insert into  tag_url(app_domain_name) values( '%s')";
    static final Cache<String, Tuple2<String, String>> tag_appname = CacheBuilder.newBuilder().concurrencyLevel(10).maximumSize(100000).expireAfterWrite(10, TimeUnit.MINUTES).build();

    public TagerUrlKeyword(JdbcConnectionPool pool) {
        super(pool);
    }


    @Override
    public Object handle(Connection client, Context context) {

            ArrayList<BehaviorData> behaviorDatas = (ArrayList<BehaviorData>) context.getContext();
            ArrayList<BehaviorData> behaviorDataList = new ArrayList<BehaviorData>(behaviorDatas.size());
            for (BehaviorData behaviorData : behaviorDatas) {
                BehaviorData.Builder behavior = behaviorData.toBuilder();
                try {
                Tuple2<String, String> tag_app = getTagAndAppName(client, behaviorData);
                if (tag_app != null) {
                    if (tag_app.a != null) behavior.addAppTags(tag_app.a);
                    if (tag_app.b != null) behavior.setAppName(tag_app.b);
                }
                } catch (Exception e) {
                    exception(this.getClass().toString(), e);
                }
                behaviorDataList.add(behavior.build());
            }
            context.setContext(behaviorDataList);

        return false;
    }


    public Tuple2<String, String> getTagAndAppName(Connection client, BehaviorData behaviorData) throws ExecutionException {
        String host = behaviorData.getHost();
        if (host.trim().isEmpty() || host == null || validate(host)) {
            return null;
        } else {
            Tuple2<String, String> t_a = tag_appname.get(host, new Callable<Tuple2<String, String>>() {
                @Override
                public Tuple2<String, String> call() throws Exception {
                    Tuple2<String, String> result = null;
                    boolean flag = true;
                    PreparedStatement preperState = null;
                    ResultSet resultSet = null;
                    Statement createState = null;
                    try {
                        preperState = client.prepareStatement(String.format(sql_searchTag, host));
                        resultSet = preperState.executeQuery();

                        while (resultSet.next()) {
                            result = new Tuple2<String, String>(resultSet.getString("basic_class"), resultSet.getString("app_name"));
                            flag = false;
                        }

                        if (flag) {
                            createState = client.createStatement();
                            boolean isok = createState.execute(String.format(sql_insert, host));
                            if (isok)
                                System.out.println("========> insert to app_basic with app_domain_name = " + host);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        if (preperState != null) preperState.close();
                        if (resultSet != null) resultSet.close();
                        if (createState != null) createState.close();
                    }

                    return result;
                }
            });
            return t_a;
        }
    }

    public boolean validate(String host) {
        return (!host.contains("."));
    }

}
