/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.handge.bigdata.resource.service.impl.monitor;

import com.handge.bigdata.dao.ProxyFactory;
import com.handge.bigdata.dao.api.IBaseDAO;
import com.handge.bigdata.dao.proxy.EsProxy;
import com.handge.bigdata.enumeration.DAOProxyEnum;
import com.handge.bigdata.enumeration.ESIndexEnum;
import com.handge.bigdata.resource.models.request.monitor.ProtocolFlowDetailParam;
import com.handge.bigdata.resource.models.request.monitor.TopOfProtocolFlowParam;
import com.handge.bigdata.resource.models.response.monitor.ProtocolFlowTop;
import com.handge.bigdata.resource.service.api.monitor.ITopOfProtocolFlow;
import com.handge.bigdata.utils.CollectionUtils;
import com.handge.bigdata.utils.PageResults;
import com.handge.bigdata.utils.StringUtils;
import com.handge.bigdata.utils.TimeUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author MaJianfu
 * @date 2018/5/21 16:35
 **/
@SuppressWarnings("Duplicates")
@Component
public class TopOfProtocolFlowImpl implements ITopOfProtocolFlow {
    /**
     * 基础数据库查询Bean
     */
    @Autowired
    IBaseDAO baseDao;

    @Override
    public Object listTopOfProtocolFlow(TopOfProtocolFlowParam topOfProtocolFlowParam) {
        List<ProtocolFlowTop> proList = proList(null);
        List<ProtocolFlowTop> result = totalList(proList, topOfProtocolFlowParam.getN());
        return result.subList(0, result.size() > topOfProtocolFlowParam.getN() ? topOfProtocolFlowParam.getN() : result.size());
    }

    @Override
    public Object listProtocolFlowDetail(ProtocolFlowDetailParam protocolFlowDetailParam) {
        List<ProtocolFlowTop> proList = proList(protocolFlowDetailParam.getProtocol());
        PageResults<ProtocolFlowTop> pageResult = CollectionUtils.getPageResult(proList, protocolFlowDetailParam.getPageNo(), protocolFlowDetailParam.getPageSize());
        return pageResult;

    }

    private List<ProtocolFlowTop> proList(String appProtocol) {
        Map<String, Object> configParamMap = baseDao.getConfigParam();
        EsProxy esProxy = ProxyFactory.createProxy(DAOProxyEnum.ES);
        TransportClient client = esProxy.getClient();
        List<ProtocolFlowTop> result = new ArrayList<>();
        String[] ES_INDEX = esProxy.generateIndices(new Date(), new Date(), ESIndexEnum.ALL);
        String esType = configParamMap.get("ES_TYPE").toString();
        SearchRequestBuilder builder = null;
        if (StringUtils.notEmpty(appProtocol)) {
            builder = client.prepareSearch(ES_INDEX).setTypes(esType)
                    .setQuery(QueryBuilders.boolQuery()
                            .must(QueryBuilders.rangeQuery("startTime").format("epoch_millis").gte(TimeUtils.getStartTime()).lte(TimeUtils.getNowTime()))
                            .filter(QueryBuilders.matchPhraseQuery("appProtocol", appProtocol))
                    )
                    .addAggregation(AggregationBuilders.terms("protocol").field("appProtocol").size(10000)
                            .subAggregation(AggregationBuilders.sum("up").field("send"))
                            .subAggregation(AggregationBuilders.sum("down").field("received"))
                    );
        } else {
            builder = client.prepareSearch(ES_INDEX).setTypes(esType)
                    .setQuery(QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("startTime").format("epoch_millis").gte(TimeUtils.getStartTime()).lte(TimeUtils.getNowTime())))
                    .addAggregation(AggregationBuilders.terms("protocol").field("appProtocol").size(10000)
                            .subAggregation(AggregationBuilders.sum("up").field("send"))
                            .subAggregation(AggregationBuilders.sum("down").field("received"))
                    );
        }
        SearchRequestBuilder appBuilder = client.prepareSearch(ES_INDEX).setTypes(esType)
                .setQuery(QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("startTime").format("epoch_millis").gte(TimeUtils.getStartTime()).lte(TimeUtils.getNowTime())))
                .addAggregation(AggregationBuilders.terms("protocol").field("appProtocol").size(10000)
                        .subAggregation(AggregationBuilders.terms("appName").field("appName").size(10000)
                                .subAggregation(AggregationBuilders.sum("upApp").field("send"))
                                .subAggregation(AggregationBuilders.sum("downApp").field("received"))
                        ));
        SearchResponse response = esProxy.action(builder);
        SearchResponse appResponse = esProxy.action(appBuilder);
        HashMap<String, ArrayList<String>> protocolMap = protocolMap(response);
        HashMap<String, ArrayList<String>> appNameMap = appNameMap(appResponse);
        esProxy.returnClient();
        HashMap<String, String> protocol_App_Map = new HashMap<>();
        for (Map.Entry<String, ArrayList<String>> appEntry : appNameMap.entrySet()) {
            ArrayList<String> list = appEntry.getValue();
            Collections.sort(list, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return -Long.compare(Long.parseLong(o1.split("\\|")[1]) + Long.parseLong(o1.split("\\|")[2]),
                            Long.parseLong(o2.split("\\|")[1]) + Long.parseLong(o2.split("\\|")[2])
                    );
                }
            });
            protocol_App_Map.put(appEntry.getKey(), list.get(0).split("\\|")[0]);
        }
        for (Map.Entry<String, ArrayList<String>> entry : protocolMap.entrySet()) {
            ProtocolFlowTop protocol = new ProtocolFlowTop();
            ArrayList<String> value = entry.getValue();
            protocol.setProtocol(entry.getKey());
            for (String str : value) {
                String[] split = str.split("\\|");
                protocol.setDownloadFlow(split[1]);
                protocol.setUploadFlow(split[0]);
            }
            if (StringUtils.notEmpty(protocol.getUploadFlow()) && StringUtils.notEmpty(protocol.getDownloadFlow())) {
                protocol.setTotalFlow(String.valueOf(Long.parseLong(protocol.getUploadFlow()) + Long.parseLong(protocol.getDownloadFlow())));
            } else if (StringUtils.isEmpty(protocol.getUploadFlow()) && StringUtils.isEmpty(protocol.getDownloadFlow())) {
                protocol.setTotalFlow("0");
            } else if (StringUtils.isEmpty(protocol.getUploadFlow()) && StringUtils.notEmpty(protocol.getDownloadFlow())) {
                protocol.setTotalFlow(String.valueOf(0 + Long.parseLong(protocol.getDownloadFlow())));
            } else {
                protocol.setTotalFlow(String.valueOf(Long.parseLong(protocol.getUploadFlow()) + 0));
            }
            protocol.setMaxFlowApp(protocol_App_Map.get(entry.getKey()));
            result.add(protocol);
        }
        // 通过比较器来实现排序
        Collections.sort(result, new Comparator<ProtocolFlowTop>() {
            @Override
            public int compare(ProtocolFlowTop o1, ProtocolFlowTop o2) {
                return -Long.compare(Long.parseLong(o1.getTotalFlow()),
                        Long.parseLong(o2.getTotalFlow())
                );
            }
        });
        return result;
    }

    private List<ProtocolFlowTop> totalList(List<ProtocolFlowTop> result, int n) {
        int i = 0;
        List<ProtocolFlowTop> list = new LinkedList<>();
        for (ProtocolFlowTop mapping : result) {
            list.add(mapping);
            i++;
            if (i == n) {
                break;
            }
        }
        return list;
    }

    private HashMap<String, ArrayList<String>> protocolMap(SearchResponse sr) {
        HashMap<String, ArrayList<String>> hashMap = new HashMap<>();
        Terms histograms = sr.getAggregations().get("protocol");
        for (Terms.Bucket histogram : histograms.getBuckets()) {
            String protocol = histogram.getKeyAsString();
            Sum up = histogram.getAggregations().get("up");
            double upStream = up.getValue();
            Sum down = histogram.getAggregations().get("down");
            double downStream = down.getValue();
            if (hashMap.get(protocol) == null) {
                hashMap.put(protocol, new ArrayList<>(Arrays.asList(new BigDecimal(upStream).toPlainString() + "|" + new BigDecimal(downStream).toPlainString())));
            } else {
                hashMap.get(protocol).add(new BigDecimal(upStream).toPlainString() + "|" + new BigDecimal(downStream).toPlainString());
            }
        }
        return hashMap;
    }

    private HashMap<String, ArrayList<String>> appNameMap(SearchResponse sr) {
        HashMap<String, ArrayList<String>> hashMap = new HashMap<>();
        Terms histograms = sr.getAggregations().get("protocol");
        for (Terms.Bucket histogram : histograms.getBuckets()) {
            String appProtocol = histogram.getKeyAsString();
            Terms appNames = histogram.getAggregations().get("appName");
            for (Terms.Bucket name : appNames.getBuckets()) {
                String appName = name.getKeyAsString();
                Sum upApp = name.getAggregations().get("upApp");
                double upStream = upApp.getValue();
                Sum downApp = name.getAggregations().get("downApp");
                double downStream = downApp.getValue();
                if (hashMap.get(appProtocol) == null) {
                    hashMap.put(appProtocol, new ArrayList<>(Arrays.asList(appName + "|" + new BigDecimal(upStream).toPlainString() + "|" + new BigDecimal(downStream).toPlainString())));
                } else {
                    hashMap.get(appProtocol).add(appName + "|" + new BigDecimal(upStream).toPlainString() + "|" + new BigDecimal(downStream).toPlainString());
                }
            }
        }
        return hashMap;
    }
}
