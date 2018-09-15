package com.handge.bigdata.resource.service.impl.monitor;

import com.handge.bigdata.dao.ProxyFactory;
import com.handge.bigdata.dao.api.IBaseDAO;
import com.handge.bigdata.dao.proxy.EsProxy;
import com.handge.bigdata.enumeration.DAOProxyEnum;
import com.handge.bigdata.enumeration.ESIndexEnum;
import com.handge.bigdata.resource.models.UserContext;
import com.handge.bigdata.resource.models.request.monitor.IpFlowDetailParam;
import com.handge.bigdata.resource.models.request.monitor.TopOfIpFlowParam;
import com.handge.bigdata.resource.models.response.monitor.IpFlowTop;
import com.handge.bigdata.resource.service.api.monitor.ITopOfIpFlow;
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
 * @date 2018/5/23 9:45
 **/
@SuppressWarnings("Duplicates")
@Component
public class TopOfIpFlowImpl implements ITopOfIpFlow {

    /**
     * 基础数据库查询Bean
     */
    @Autowired
    IBaseDAO baseDao;

    @Override
    public Object listTopOfIpFlow(TopOfIpFlowParam topOfIpFlowParam) {
        List<IpFlowTop> flowTopList = ipFlowTopListList(topOfIpFlowParam.getUserContext(), null);
        List<IpFlowTop> result = totalList(flowTopList, topOfIpFlowParam.getN());
        return result.subList(0, result.size() > topOfIpFlowParam.getN() ? topOfIpFlowParam.getN() : result.size());
    }

    @Override
    public Object listIpFlowDetail(IpFlowDetailParam ipFlowDetailParam) {
        List<IpFlowTop> flowTopList = ipFlowTopListList(ipFlowDetailParam.getUserContext(), ipFlowDetailParam.getIp());
        PageResults<IpFlowTop> pageResult = CollectionUtils.getPageResult(flowTopList, ipFlowDetailParam.getPageNo(), ipFlowDetailParam.getPageSize());
        return pageResult;
    }

    private List<IpFlowTop> ipFlowTopListList(UserContext context, String ip) {
        EsProxy esProxy = ProxyFactory.createProxy(DAOProxyEnum.ES);
        TransportClient client = esProxy.getClient();
        List<IpFlowTop> result = new ArrayList<>();
        String[] ES_INDEX = esProxy.generateIndices(new Date(), new Date(), ESIndexEnum.ALL);
        SearchRequestBuilder builder = null;
        Map<String, Object> configParamMap = baseDao.getConfigParam();
        String esType = configParamMap.get("ES_TYPE").toString();
        if (StringUtils.notEmpty(ip)) {
            builder = client.prepareSearch(ES_INDEX).setTypes(esType)
                    .setQuery(QueryBuilders.boolQuery()
                            .must(QueryBuilders.rangeQuery("startTime").format("epoch_millis").gte(TimeUtils.getStartTime()).lte(TimeUtils.getNowTime()))
                            .filter(QueryBuilders.matchPhraseQuery("localIp", ip))
                    )
                    .addAggregation(AggregationBuilders.terms("ip").field("localIp").size(10000)
                            .subAggregation(AggregationBuilders.sum("up").field("send"))
                            .subAggregation(AggregationBuilders.sum("down").field("received"))
                    );
        } else {
            builder = client.prepareSearch(ES_INDEX).setTypes(esType)
                    .setQuery(QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("startTime").format("epoch_millis").gte(TimeUtils.getStartTime()).lte(TimeUtils.getNowTime())))
                    .addAggregation(AggregationBuilders.terms("ip").field("localIp").size(10000)
                            .subAggregation(AggregationBuilders.sum("up").field("send"))
                            .subAggregation(AggregationBuilders.sum("down").field("received"))
                    );
        }
        SearchRequestBuilder appBuilder = client.prepareSearch(ES_INDEX).setTypes(esType)
                .setQuery(QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery("startTime").format("epoch_millis").gte(TimeUtils.getStartTime()).lte(TimeUtils.getNowTime())))
                .addAggregation(AggregationBuilders.terms("ip").field("localIp").size(10000)
                        .subAggregation(AggregationBuilders.terms("appName").field("appName").size(10000)
                                .subAggregation(AggregationBuilders.sum("upApp").field("send"))
                                .subAggregation(AggregationBuilders.sum("downApp").field("received"))
                        ));
        SearchResponse response = esProxy.action(builder);
        SearchResponse appResponse = esProxy.action(appBuilder);
        HashMap<String, ArrayList<String>> ipMap = ipMap(response);
        HashMap<String, ArrayList<String>> appNameMap = appNameMap(appResponse);
        esProxy.returnClient();
        HashMap<String, String> ip_App_Map = new HashMap<>();
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
            ip_App_Map.put(appEntry.getKey(), list.get(0).split("\\|")[0]);
        }
        for (Map.Entry<String, ArrayList<String>> entry : ipMap.entrySet()) {
            IpFlowTop ips = new IpFlowTop();
            ArrayList<String> value = entry.getValue();
            ips.setIP(entry.getKey());
            for (String str : value) {
                String[] split = str.split("\\|");
                ips.setDownloadFlow(split[1]);
                ips.setUploadFlow(split[0]);
            }
            if (StringUtils.notEmpty(ips.getUploadFlow()) && StringUtils.notEmpty(ips.getDownloadFlow())) {
                ips.setTotalFlow(String.valueOf(Long.parseLong(ips.getUploadFlow()) + Long.parseLong(ips.getDownloadFlow())));
            } else if (StringUtils.isEmpty(ips.getUploadFlow()) && StringUtils.isEmpty(ips.getDownloadFlow())) {
                ips.setTotalFlow("0");
            } else if (StringUtils.isEmpty(ips.getUploadFlow()) && StringUtils.notEmpty(ips.getDownloadFlow())) {
                ips.setTotalFlow(String.valueOf(0 + Long.parseLong(ips.getDownloadFlow())));
            } else {
                ips.setTotalFlow(String.valueOf(Long.parseLong(ips.getUploadFlow()) + 0));
            }
            ips.setMaxFlowApp(ip_App_Map.get(entry.getKey()));
            result.add(ips);
        }
        // 通过比较器来实现排序
        Collections.sort(result, new Comparator<IpFlowTop>() {
            @Override
            public int compare(IpFlowTop o1, IpFlowTop o2) {
                return -Long.compare(Long.parseLong(o1.getTotalFlow()),
                        Long.parseLong(o2.getTotalFlow())
                );
            }
        });
        return result;
    }

    private List<IpFlowTop> totalList(List<IpFlowTop> result, int n) {
        int i = 0;
        List<IpFlowTop> list = new LinkedList<>();
        for (IpFlowTop mapping : result) {
            list.add(mapping);
            i++;
            if (i == n) {
                break;
            }
        }
        return list;
    }

    private HashMap<String, ArrayList<String>> ipMap(SearchResponse sr) {
        HashMap<String, ArrayList<String>> hashMap = new HashMap<>();
        Terms histograms = sr.getAggregations().get("ip");
        for (Terms.Bucket histogram : histograms.getBuckets()) {
            String ip = histogram.getKeyAsString();
            Sum up = histogram.getAggregations().get("up");
            double upStream = up.getValue();
            Sum down = histogram.getAggregations().get("down");
            double downStream = down.getValue();
            if (hashMap.get(ip) == null) {
                hashMap.put(ip, new ArrayList<>(Arrays.asList(new BigDecimal(upStream).toPlainString() + "|" + new BigDecimal(downStream).toPlainString())));
            } else {
                hashMap.get(ip).add(new BigDecimal(upStream).toPlainString() + "|" + new BigDecimal(downStream).toPlainString());
            }
        }
        return hashMap;
    }

    private HashMap<String, ArrayList<String>> appNameMap(SearchResponse sr) {
        HashMap<String, ArrayList<String>> hashMap = new HashMap<>();
        Terms histograms = sr.getAggregations().get("ip");
        for (Terms.Bucket histogram : histograms.getBuckets()) {
            String ip = histogram.getKeyAsString();
            Terms appNames = histogram.getAggregations().get("appName");
            for (Terms.Bucket name : appNames.getBuckets()) {
                String appName = name.getKeyAsString();
                Sum upApp = name.getAggregations().get("upApp");
                double upStream = upApp.getValue();
                Sum downApp = name.getAggregations().get("downApp");
                double downStream = downApp.getValue();
                if (hashMap.get(ip) == null) {
                    hashMap.put(ip, new ArrayList<>(Arrays.asList(appName + "|" + new BigDecimal(upStream).toPlainString() + "|" + new BigDecimal(downStream).toPlainString())));
                } else {
                    hashMap.get(ip).add(appName + "|" + new BigDecimal(upStream).toPlainString() + "|" + new BigDecimal(downStream).toPlainString());
                }
            }
        }
        return hashMap;
    }

}
