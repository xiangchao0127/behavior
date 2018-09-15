/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.handge.bigdata.base;

import com.handge.bigdata.pools.EnvironmentContainer;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WrapperQueryBuilder;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ElasticSearchBase extends ComponentBaseHandler implements DataOperation {


    public ElasticSearchBase() {
        super();
    }

    public static void main(String[] args) {

        EnvironmentContainer.setENV();

        ElasticSearchBase elasticSearchBase = new ElasticSearchBase();

        Object s = elasticSearchBase.testES("dataflow", "tweet", "{\"match_all\": {}}");
        System.out.println(s);


    }

    @Override
    public Object delete(Object... objects) {
        return null;
    }

    @Override
    public Object update(Object... objects) {
        return null;
    }

    @Override
    public Object searche(Object... objects) {
        return null;
    }

    @Override
    public Object create(Object... objects) {
        return null;
    }

    @Override
    public Object get(Object... objects) {
        return null;
    }

    //TODO Just for test
    public Object testES(String index, String type, Object... objects) {
        ArrayList<Object> rsr = new ArrayList<>();
        for (final Object object : objects) {
            Object rs = new WrapTaskUseEsTrspCli() {
                @Override
                public Object call() throws Exception {
                    WrapperQueryBuilder query = QueryBuilders.wrapperQuery(String.valueOf(object));
                    System.out.println(query.toString());
                    return transportClient.prepareSearch(index).setTypes(type).setSize(1000).setQuery(query).execute().get();
                }
            }.run();
            rsr.add(rs);
        }

        return rsr;
    }

    public Object testEsAndMysql() {
        return new WrapTaskUseEsTrspCliAndMysql<Object>() {
            @Override
            public Object call() throws Exception {
                ResultSet mysqlResults = this.mysqlConnection.prepareStatement("select * from dataflow").executeQuery();
                SearchResponse esResults = this.transportClient.prepareSearch("dataflow").get();
                return null;
            }
        }.run();
    }


    // todo   many use case  ......

    public Object testKafkaproducer() {
        return new WrapTaskUseKafkaProducer<Object>() {

            @Override
            public Object call() throws Exception {
                this.producer.send(null);
                return null;
            }
        }.run(100, TimeUnit.MILLISECONDS);
    }


}
