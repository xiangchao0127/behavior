/*
 * Copyright (c) 2018  Inc. All rights reserved
 * Projects : net-behavior
 * Module : behavior-calculation
 * Class : ElasticWriter
 * User : XueFei Wang
 * Date : 5/31/18 10:46 AM
 * Modified :5/31/18 10:21 AM
 * Todo :
 *
 */

package com.handge.bigdata.handlers;

import com.handge.bigdata.handlerchain.BackendHandler;
import com.handge.bigdata.handlerchain.Context;
import com.handge.bigdata.pools.es.EsConnectionPool;
import com.handge.bigdata.proto.Behavior.BehaviorData;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ElasticWriter extends BackendHandler<TransportClient, EsConnectionPool> {

    protected final Calendar calendar = Calendar.getInstance();

    protected final String indexMode = "%s_%02d";

    protected final String indexModeNoise = "%s_%02d_noise";

    protected final String type = "data";

    public ElasticWriter(EsConnectionPool pool) {
        super(pool);
    }

    @Override
    public Object handle(TransportClient client, Context context) {
        try {
            ArrayList<BehaviorData> behaviorDatas = (ArrayList<BehaviorData>) context.getContext();
            BulkRequestBuilder bulk = client.prepareBulk();
            for (BehaviorData behaviorData : behaviorDatas) {
                String index = getOrCreateIndex(behaviorData.getStartTime(), behaviorData.getIsnoise(), client.admin().indices());
                IndexRequest indexRequest = getIndexRequest(behaviorData, index);
                bulk.add(indexRequest);
            }
            bulk.execute(new ActionListener<BulkResponse>() {
                @Override
                public void onResponse(BulkResponse bulkItemResponses) {

                }

                @Override
                public void onFailure(Exception e) {
                    exception(this.getClass().getName(), e);
                }
            });

        } catch (Exception e) {
            exception(this.getClass().getName(), e);
        }
        return null;
    }


    protected IndexRequest getIndexRequest(BehaviorData behaviorData, String index) {
        IndexRequest indexRequest = new IndexRequest(index, type);
        indexRequest.source(data2Map(behaviorData));
        return indexRequest;
    }

    protected Map data2Map(BehaviorData behaviorData) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("appName", behaviorData.getAppName());
        data.put("appProtocol", behaviorData.getAppProtocol());
        data.put("appType", behaviorData.getAppType());
        data.put("appTags", behaviorData.getAppTagsList());
        data.put("localIp", behaviorData.getLocalIp());
        data.put("desIp", behaviorData.getDesIp());
        data.put("info", behaviorData.getDetails());
        data.put("device", behaviorData.getDeviceId());
        data.put("received", behaviorData.getReceive());
        data.put("send", behaviorData.getSend());
        data.put("startTime", behaviorData.getStartTime());
        data.put("endTime", behaviorData.getEndTime());
        data.put("url", behaviorData.getHttpUrl());
        data.put("method", behaviorData.getHttpMethod());
        data.put("clientType", behaviorData.getClient());
        data.put("serverType", behaviorData.getServer());
        data.put("domain", behaviorData.getHost());
        return data;
    }

    protected String getOrCreateIndex(Long time, boolean mode, IndicesAdminClient adminClient) throws Exception {
        calendar.setTimeInMillis(time);
        String index = null;
        if (mode) {
            index = String.format(indexModeNoise, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
        } else {
            index = String.format(indexMode, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
        }
        boolean exists = adminClient.prepareExists(index).get().isExists();
        if (!exists) {
            synchronized (this) {
                boolean check = adminClient.prepareExists(index).get().isExists();
                if (!check) {
                    CreateIndexRequestBuilder createdIndex = adminClient.prepareCreate(index);
                    createdIndex.addMapping(type, createMapping());
                    boolean issuccessful = createdIndex.get().isAcknowledged();
                    if (issuccessful) {
                        throw new Exception("could`t create index " + index);
                    }
                }
            }
        }
        return index;

    }


    private XContentBuilder createMapping() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject(type).startObject("properties");
        builder.startObject("appName").field("type", "keyword").endObject();
        builder.startObject("appProtocol").field("type", "keyword").endObject();
        builder.startObject("appType").field("type", "keyword").endObject();
        builder.startObject("appTags").field("type", "keyword").endObject();
        builder.startObject("localIp").field("type", "keyword").endObject();
        builder.startObject("desIp").field("type", "keyword").endObject();
        builder.startObject("info").field("type", "keyword").endObject();
        builder.startObject("device").field("type", "keyword").endObject();
        builder.startObject("received").field("type", "long").endObject();
        builder.startObject("send").field("type", "long").endObject();
        builder.startObject("startTime").field("type", "long").endObject();
        builder.startObject("endTime").field("type", "long").endObject();
        builder.startObject("url").field("type", "text").endObject();
        builder.startObject("method").field("type", "keyword").endObject();
        builder.startObject("clientType").field("type", "text").endObject();
        builder.startObject("serverType").field("type", "text").endObject();
        builder.startObject("domain").field("type", "text").endObject();
        builder.endObject().endObject().endObject();

        return builder;
    }
}





