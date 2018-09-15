/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.handge.bigdata;

import org.apache.http.HttpHost;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.client.RestClient;

import java.io.IOException;

public class TestEsProxy {
    public static void main(String[] args) throws IOException {


        RestClient client = RestClient.builder(new HttpHost("datanode1", 9200, "http"),
                new HttpHost("datanode2", 9200, "http")).build();


        ResponseListener responseListener = new ResponseListener() {

            @Override
            public void onSuccess(Response response) {
                try {
                    System.out.println(EntityUtils.toString(response.getEntity()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception exception) {

            }
        };
        client.performRequestAsync("GET", "/dataflow/_search", responseListener);

        client.performRequest("GET", "/dataflow/_search", null);

    }


}
