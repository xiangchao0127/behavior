package com.handge.bigdata.resource.service.impl.monitor;

import com.handge.bigdata.base.ComponentBaseHandler;
import com.handge.bigdata.dao.ProxyFactory;
import com.handge.bigdata.dao.api.IBaseDAO;
import com.handge.bigdata.dao.proxy.EsProxy;
import com.handge.bigdata.enumeration.DAOProxyEnum;
import com.handge.bigdata.enumeration.DateFormatEnum;
import com.handge.bigdata.enumeration.ESIndexEnum;
import com.handge.bigdata.enumeration.TimeModelEnum;
import com.handge.bigdata.resource.models.request.monitor.FlowTendencyParam;
import com.handge.bigdata.resource.models.response.monitor.FlowOfNonWorkingApp;
import com.handge.bigdata.resource.models.response.monitor.FlowTendency;
import com.handge.bigdata.resource.service.api.monitor.IFlowTendency;
import com.handge.bigdata.utils.DateUtil;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by DaLu Guo on 2018/5/4.
 */
@Component
public class FlowTendencyImpl extends ComponentBaseHandler implements IFlowTendency {

    /**
     * 基础数据库查询Bean
     */
    @Autowired
    IBaseDAO baseDAO;

    @Override
    public Object listFlowTendency(FlowTendencyParam flowTendencyParam) {

        Map<String, Object> configMap = baseDAO.getConfigParam();
        /**
         * 1.通过ES函数查询数据
         */

        SearchResponse response = searchFromES(flowTendencyParam.getModel(), configMap);

        /**
         * 2.对查询结果按照时间、应用名，平均每5m/2s进行流量统计
         */

        List<Object[]> countFlowResult = countFlow(response, flowTendencyParam.getSize());

        /**
         * 3,封装到前端DTO中
         */
        List<FlowTendency> result = returnResult(countFlowResult);

        return result;
    }

    //查询ES数据
    private SearchResponse searchFromES(String model, Map<String, Object> configMap) {
        EsProxy esProxy = ProxyFactory.createProxy(DAOProxyEnum.ES);
        TransportClient esClient = esProxy.getClient();

        QueryBuilder queryBuilder = null;
        DateHistogramInterval dateHistogramInterval = null;


        if (TimeModelEnum.实时.getModel().equals(model)) {
            long startTimeStamp = DateUtil.getTimeByMinute(-3);
            long endTimeStamp = System.currentTimeMillis();
            queryBuilder = QueryBuilders.rangeQuery("startTime").from(startTimeStamp, true).to(endTimeStamp, true);
            dateHistogramInterval = DateHistogramInterval.seconds(2);
        } else {
            long startTimeStamp = 0;
            try {
                startTimeStamp = DateUtil.dateToStartOrEndStamp(DateUtil.timeStampToStrDate(System.currentTimeMillis(), DateFormatEnum.SECONDS), 0);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long endTimeStamp = System.currentTimeMillis();
            queryBuilder = QueryBuilders.rangeQuery("startTime").from(startTimeStamp, true).to(endTimeStamp, true);
            dateHistogramInterval = DateHistogramInterval.minutes(5);
        }
        String today = DateUtil.timeStampToStrDate(System.currentTimeMillis(), DateFormatEnum.DAY);
        String[] indices = esProxy.generateIndices(today, today, ESIndexEnum.ALL);
        String esType = configMap.get("ES_TYPE").toString();
        SearchRequestBuilder builder = esClient.prepareSearch(indices)
                .setTypes(esType)
                .setQuery(QueryBuilders.boolQuery().filter(queryBuilder))
                .addAggregation(AggregationBuilders
                        .dateHistogram("time")
                        .field("startTime").dateHistogramInterval(dateHistogramInterval)
                        .subAggregation(AggregationBuilders.terms("appName").field("appName").size(10000)
                                .subAggregation(AggregationBuilders.sum("upstreamFlow").field("send"))
                                .subAggregation(AggregationBuilders.sum("downstreamFlow").field("received"))
                        )
                );
        SearchResponse response = esProxy.action(builder);
        esProxy.returnClient();
        return response;
    }

    private List<Object[]> countFlow(SearchResponse response, int size) {
        List<Object[]> countFlowResult = new ArrayList<>();
        Histogram dateHistogram = response.getAggregations().get("time");
        // For each time entry
        for (Histogram.Bucket timeEntry : dateHistogram.getBuckets()) {
            //时间段
            String time = timeEntry.getKeyAsString();
            //每个时间段的总流量
            Double totalFlow = 0.0;
            //每个时间段的总上传流量
            Double totalUploadFlow = 0.0;
            //每个时间段的总下载流量
            Double totalDownloadFlow = 0.0;
            //统计该时间段每个应用的流量
            Object[] countByApp;
            //统计排名前三的应用
            List<Object[]> appTops = new ArrayList();

            Terms appNameTerm = timeEntry.getAggregations().get("appName");
            // For each appName entry
            for (Terms.Bucket appNameEntry : appNameTerm.getBuckets()) {
                countByApp = new Object[4];
                Sum totalUpSum = appNameEntry.getAggregations().get("upstreamFlow");
                double upFlow = totalUpSum.getValue();
                Sum totalDownSum = appNameEntry.getAggregations().get("downstreamFlow");
                double downFlow = totalDownSum.getValue();
                String appName = appNameEntry.getKeyAsString();
                totalUploadFlow += upFlow;
                totalDownloadFlow += downFlow;
                totalFlow = (totalUploadFlow + totalDownloadFlow);
                countByApp[0] = (appName);
                countByApp[1] = (upFlow);
                countByApp[2] = (downFlow);
                countByApp[3] = (upFlow + downFlow);
                //对每个应用进行流量排名
                if (appTops.size() > 0) {
                    for (int index = 0; index < appTops.size(); index++) {
                        if ((double) countByApp[3] > (double) appTops.get(index)[3]) {
                            appTops.add(index, countByApp);
                            break;
                        } else if (index == appTops.size() - 1) {
                            appTops.add(countByApp);
                            break;
                        }
                    }
                } else {
                    appTops.add(countByApp);
                }
            }
            countFlowResult.add(new Object[]{time, totalFlow, totalUploadFlow, totalDownloadFlow, appTops.size() < size ? appTops : appTops.subList(0, size)});
        }
        return countFlowResult;
    }

    private List<FlowTendency> returnResult(List<Object[]> countFlowResult) {
        List<FlowTendency> result = new ArrayList<>();
        for (Object[] obj : countFlowResult) {
            FlowTendency flowTendency = new FlowTendency();
            flowTendency.setTime(DateUtil.timeStampToStrDate((Long.parseLong(obj[0].toString())), DateFormatEnum.SECONDS));
            flowTendency.setTotalFlow(obj[1].toString());
            flowTendency.setUploadFlow(obj[2].toString());
            flowTendency.setDownloadFlow(obj[3].toString());
            List<FlowOfNonWorkingApp> flowOfNonWorkingAppList = new ArrayList<>();
            for (Object[] appFlowObj : (List<Object[]>) obj[4]) {
                FlowOfNonWorkingApp flowOfNonWorkingApp = new FlowOfNonWorkingApp();
                flowOfNonWorkingApp.setAppName(appFlowObj[0].toString());
                flowOfNonWorkingApp.setUploadFlow(appFlowObj[1].toString());
                flowOfNonWorkingApp.setDownloadFlow(appFlowObj[2].toString());
                flowOfNonWorkingApp.setTotalFlow(appFlowObj[3].toString());
                flowOfNonWorkingAppList.add(flowOfNonWorkingApp);
            }
            flowTendency.setFlowOfNonWorkingAppList(flowOfNonWorkingAppList);
            result.add(flowTendency);
        }
        return result;
    }

}
