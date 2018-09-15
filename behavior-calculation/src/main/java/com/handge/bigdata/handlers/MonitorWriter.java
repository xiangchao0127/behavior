/*
 * Copyright (c) 2018  Inc. All rights reserved
 * Projects : net-behavior
 * Module : behavior-calculation
 * Class : Monitor
 * User : XueFei Wang
 * Date : 6/25/18 4:52 PM
 * Modified :6/25/18 4:52 PM
 * Todo :
 *
 */

package com.handge.bigdata.handlers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.handge.bigdata.base.Tuple3;
import com.handge.bigdata.handlerchain.BackendHandler;
import com.handge.bigdata.handlerchain.Context;
import com.handge.bigdata.pools.jdbc.JdbcConnectionPool;
import com.handge.bigdata.proto.Behavior.BehaviorData;
import com.handge.bigdata.tree.TreeNode;
import com.handge.bigdata.tree.multinode.ArrayMultiTreeNode;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MonitorWriter  extends BackendHandler<Connection, JdbcConnectionPool> {

    static final  String sql_search = "select *  from lib_abnormal_rules  where status = 1";
    static final  TreeNode<String> me = new ArrayMultiTreeNode("*");
    static final  TreeNode<String> root = new ArrayMultiTreeNode<>("root");
    static final  String sql_insert = "insert into  app_abnormal_info (accessTime,appClass,appName,appProtocol,attr,create_at,detail,downFlow,lev,localIP,number,targetIP,typ,upFlow) " +
                                                         "values  ( ? ,   ?,   ?,   ?,     ?,     ?,     ?,    ?,    ?,    ?,    ?,    ?,  ?,   ?)";
    static final  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static Cache<String, TreeNode<String>> map = CacheBuilder.newBuilder().concurrencyLevel(10).expireAfterWrite(1, TimeUnit.MINUTES).build();

    public MonitorWriter(JdbcConnectionPool pool) {
        super(pool);
    }

    @Override
    public Object handle(Connection client, Context context) {
        ArrayList<BehaviorData> behaviorDatas = (ArrayList<BehaviorData>) context.getContext();
       for (BehaviorData data : behaviorDatas) {
            if (data.getIsnoise()){
                continue;
            }
            try {
                ArrayList<Tuple3<String, Integer,String>> illegals = illegal(client,data);
                if ( !illegals.isEmpty()){
                    report(client,  data , illegals);
                }
            }catch (Exception e    ){
                exception("",e);
            }
       }
        return null;
    }

    public ArrayList<Tuple3<String, Integer,String>>  illegal(Connection connection, BehaviorData data) {
         TreeNode<String> protocol = new ArrayMultiTreeNode<>(data.getAppProtocol());
         String domain = data.getHost();
         String url = data.getHttpUrl();
         long time = data.getStartTime();
         String localIp = data.getLocalIp();
         ArrayList<Tuple3<String,Integer,String>> relusts = new ArrayList<>();
        try {
            TreeNode<String>  tree  =  getCacheTree(connection);
            if (tree == null) return null;
            TreeNode<String> protocol_node = tree.findInNextLevel(protocol.data());
            TreeNode<String> protocol_star = tree.findInNextLevel(me.data());
            ArrayList<TreeNode<String>> pro_domains = new ArrayList<>();
            if (protocol_node != null){
                pro_domains.addAll(protocol_node.subtrees());
            }
            if (protocol_star != null) {
                pro_domains.addAll(protocol_star.subtrees());
            }
            if (pro_domains.size() == 0){
                return  null;
            }

            for (TreeNode<String> i_domain : pro_domains){
                if (i_domain == null) continue;

                if (domain.contains(i_domain.data()) || i_domain.valueEquals(me))  {
                    Collection<? extends TreeNode<String>> i_keywords = i_domain.subtrees();
                    if (i_keywords == null || i_keywords.isEmpty()){
                        continue;
                    }
                    for (TreeNode<String> i_keyword : i_keywords){
                        boolean match = false;
                        if (i_keyword.valueEquals(me)){
                            match = true;
                        }else {
                            String[] keywords = i_keyword.data().split(",");
                            Set<String> ks = getUrlParamters(url);
                            for (String key : keywords) {
                                if (ks.contains(key)) {
                                    match = true;
                                    break;
                                }
                            }
                        }
                        if (match){
                            Collection<? extends TreeNode<String>> timeRanges = i_keyword.subtrees();
                            if (timeRanges == null || timeRanges.isEmpty()){
                                continue;
                            }
                            for (TreeNode<String> timeR : timeRanges){
                                String[] times = timeR.data().split(",");
                                if (times.length != 2) {
                                    exception( timeR.parent().toString(),new RuntimeException(" time range  exception"));
                                    continue;
                                }else {
                                    long timeA = 0;
                                    long timeB = 0;
                                    try {
                                        timeA = sdf.parse(times[0]).getTime();
                                        timeB = sdf.parse(times[1]).getTime();
                                        if (timeB < timeA){
                                            exception( timeR.parent().toString(),new RuntimeException(" end time must bigger than starttime"));
                                            continue;
                                        }
                                    }catch (Exception e){
                                        exception( timeR.parent().toString(),new RuntimeException(" time parser exception"));
                                        continue;
                                    }
                                    if (timeA <= time && time <= timeB){   // time match
                                        Collection<? extends TreeNode<String>> ipsAreas = timeR.subtrees();
                                        if (ipsAreas == null || ipsAreas.isEmpty()){
                                            continue;
                                        }
                                        for (TreeNode<String> ipArea : ipsAreas){
                                            if (ipArea == null){
                                                continue;
                                            }
                                            String ip = ipArea.data();
                                            if (localIp.startsWith(ip)){
                                                TreeNode<String> node = ipArea.subtrees().iterator().next();
                                                String className = node.data();
                                                node = node.subtrees().iterator().next();
                                                int level = Integer.valueOf(node.data());
                                                node = node.subtrees().iterator().next();
                                                String type = node.data();
                                                Tuple3<String,Integer,String> tuple3 = new Tuple3(className,level,type);
                                                relusts.add(tuple3);
                                                continue;
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return  relusts;
    }



    public Set<String> getUrlParamters(String url) throws URISyntaxException {
        Set<String> parameters  = new HashSet<>();
        if (url != null || !url.isEmpty()) {

            List<NameValuePair> kvs = URLEncodedUtils.parse(new URI(url), "UTF-8");

            for (NameValuePair kv : kvs) {
                parameters.add(kv.getValue());
            }
        }
        return parameters;
    }

    /**
     *                                     root
     *                                 /  /    \   \  \
     *                                   *      protos
     *                                  /        \  \
     *                                domain      *
     *                                /
     *                                keywords
     *                                \
     *                                timerangs
     *                                /
     *                              ips
     *                               /
     *                         class   -level -   type
     * @param connection
     * @return
     * @throws ExecutionException
     */
    public TreeNode<String> getCacheTree(Connection connection) throws ExecutionException {
        TreeNode<String> cache  = map.get(root.data(), new Callable<TreeNode<String>>() {
            @Override
            public TreeNode<String> call() throws Exception {
                PreparedStatement prepareStatament = null;
                ResultSet resultSet = null;
                try {
                    prepareStatament = connection.prepareStatement(sql_search);
                    resultSet = prepareStatament.executeQuery();
                    while (resultSet.next()){
                        String proto = resultSet.getString("proto");
                        String domain = resultSet.getString("domain").replaceAll("\\*","").trim();
                        String keyWords = resultSet.getString("url_keyword");
                        String className = resultSet.getString("class");
                        String level = resultSet.getString("level");
                        String time_start = resultSet.getString("monitor_start_time");
                        String time_end = resultSet.getString("monitor_end_time");
                        String ipareas = resultSet.getString("monitor_range").replaceAll("\\*","").trim();
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

                }catch ( Exception e){
                }finally {
                    if (prepareStatament != null) {
                        prepareStatament.close();
                    }
                    if (resultSet != null){
                        resultSet.close();
                    }
                }
                return root;
            }
        });
       return cache;
    }

    public void report(Connection connection, BehaviorData data,  ArrayList<Tuple3<String, Integer,String>> illegals) throws SQLException {
        PreparedStatement prepareStatement = null;
        try {

            //illegal_info (accessTime,appClass,appName,appProtocol,attri,create_at,detail,downFlow,levol,localIP,number,targetIP,typer,upFlow) " +
            //       "values  ( 1 ,       2,        3        4         5      6        7        8      9     10     11        12    13     14    ?,   ?,     ?,     ?,     ?,    ?,    ?,    ?,    ?,    ?,  ?,   ?)";
             prepareStatement = connection.prepareStatement(sql_insert);
             prepareStatement.setDate(1,new Date(data.getStartTime()));
             prepareStatement.setString(2,data.getAppType());
             prepareStatement.setString(3,data.getAppName());
             prepareStatement.setString(4,data.getAppProtocol());
             prepareStatement.setDate(6, new Date(System.currentTimeMillis()));
             prepareStatement.setString(7,data.getDetails());
             prepareStatement.setLong(8,data.getReceive());
             prepareStatement.setString(10,data.getLocalIp());
             prepareStatement.setString(11,"");
             prepareStatement.setString(12,data.getHost());
             prepareStatement.setLong(14,data.getSend());
            for (Tuple3<String, Integer,String> tuple3 : illegals){
                String classes = tuple3.get_1();
                int level = tuple3.get_2();
                String type = tuple3.get_3();
                prepareStatement.setString(13,classes);
                prepareStatement.setInt(9,level);
                prepareStatement.setInt(5,Integer.valueOf(type));
                prepareStatement.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
          if (prepareStatement != null )  prepareStatement.close();
        }

    }
}
