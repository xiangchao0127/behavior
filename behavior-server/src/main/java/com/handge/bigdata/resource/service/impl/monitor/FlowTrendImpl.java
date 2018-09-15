package com.handge.bigdata.resource.service.impl.monitor;

import com.handge.bigdata.UnifiedException;
import com.handge.bigdata.dao.ProxyFactory;
import com.handge.bigdata.dao.api.IBaseDAO;
import com.handge.bigdata.dao.proxy.EsProxy;
import com.handge.bigdata.enumeration.DAOProxyEnum;
import com.handge.bigdata.enumeration.DateFormatEnum;
import com.handge.bigdata.enumeration.ESIndexEnum;
import com.handge.bigdata.resource.models.request.monitor.FlowTrendParam;
import com.handge.bigdata.resource.models.response.monitor.CountryVisitInfo;
import com.handge.bigdata.resource.models.response.monitor.FlowTrend;
import com.handge.bigdata.resource.service.api.monitor.IFlowTrend;
import com.handge.bigdata.utils.DateUtil;
import com.handge.bigdata.utils.NumberUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.*;

/**
 * Created by DaLu Guo on 2018/5/4.
 *
 * @author Liujuhao
 * @date 2018/5/17
 */
@Component
public class FlowTrendImpl implements IFlowTrend {

    Log logger = LogFactory.getLog(this.getClass());

    /**
     * 如何IP不存在于任何网段，或程序异常时的缺省值
     */
    private static String UNKNOWN_IP_COUNTRY = "未知";

    /**
     * 系统部署所在境内名称
     */
    private static String LOCAL_COUNTRY_NAME = "中国";

    /**
     * 基础数据库查询Bean
     */
    private final IBaseDAO BASE_DAO;

    private Map<Long, Object[]> COUNTRY_IPS_MAP;

    private List<Long> COUNTRY_KEYS;

    private Map<String, Object[]> COUNTRY_INFO_MAP;

    @Autowired
    public FlowTrendImpl(IBaseDAO BASE_DAO) {
        this.BASE_DAO = BASE_DAO;
        this.COUNTRY_IPS_MAP = BASE_DAO.getIpRegionOfCountry();
        this.COUNTRY_KEYS = new ArrayList(COUNTRY_IPS_MAP.keySet());
        this.COUNTRY_INFO_MAP = BASE_DAO.getInfoOfCountry();
    }

    @Override
    public Object flowTrend(FlowTrendParam flowTrendParam) {
        Map<String, Object> configParamMap = BASE_DAO.getConfigParam();
        /**
         * 1.通过ES函数查询数据
         */
        SearchResponse sr = searchForES(configParamMap);
        /**
         * 2.1将ES结果进行转化，获取国家-访问次数
         */
        Map<String, Long> esMap = wrapEsResult(sr);

        /**
         * 2.2将ES结果进行转化，获取公司人员的集合
         */
        Set<String> users = wrapEsResutUser(sr);
        /**
         * 3.封装到前端DTO中
         */
        FlowTrend result = transforToFrontResult(esMap, users);

        return result;
    }

    private SearchResponse searchForES(Map<String, Object> configParamMap) {
        String today = DateUtil.timeStampToStrDate(System.currentTimeMillis(), DateFormatEnum.SECONDS);
        long start = 0;
        try {
            start = DateUtil.dateToStartOrEndStamp(today, 0);
        } catch (ParseException e) {
            throw new UnifiedException(e);
        }
        long end = System.currentTimeMillis();
        EsProxy esProxy = ProxyFactory.createProxy(DAOProxyEnum.ES);
        String[] indices = esProxy.generateIndices(new Date(), new Date(), ESIndexEnum.MAPPING);
        TransportClient esClient = esProxy.getClient();
        String esType = configParamMap.get("ES_TYPE").toString();
        SearchRequestBuilder builder = esClient.prepareSearch(indices)
                .setTypes(esType)
                .setQuery(QueryBuilders.boolQuery()
                        .filter(QueryBuilders.rangeQuery("startTime").from(start, true).to(end, false))
                        .filter(QueryBuilders.termQuery("received", 0))
                )
                .addAggregation(AggregationBuilders.terms("destinationIP").field("desIp").size(10000)
                )
                .addAggregation(AggregationBuilders.terms("sourceIP").field("localIp").size(10000));

        logger.debug(builder);

        SearchResponse sr = esProxy.action(builder);

        esProxy.returnClient();

        return sr;
    }

    private Map<String, Long> wrapEsResult(SearchResponse sr) {
        Map<String, Long> wrapResult = new HashMap<>();
        Terms destinationIPs = sr.getAggregations().get("destinationIP");
        for (Terms.Bucket destinationIP : destinationIPs.getBuckets()) {
            String ip = destinationIP.getKeyAsString();
            long value = destinationIP.getDocCount();
            String country = matchCountryByIp(ip);
            if (wrapResult.containsKey(country)) {
                wrapResult.put(country, wrapResult.get(country) + value);
            } else {
                wrapResult.put(country, value);
            }
        }

        return wrapResult;
    }

    private FlowTrend transforToFrontResult(Map<String, Long> map, Set<String> users) {
        List<String> churchyardCountry = Arrays.asList(LOCAL_COUNTRY_NAME);
        //显示前N个
        int topN = 5;

        FlowTrend result = new FlowTrend();
        List<CountryVisitInfo> countryVisitInfos = new LinkedList<>();
        List<Map.Entry<String, Long>> lst = new ArrayList<>();
        lst.addAll(map.entrySet());
        Collections.sort(lst, (o1, o2) -> o2.getValue().intValue() - o1.getValue().intValue());
        for (int i = 0; i < lst.size() && i < topN; i++) {
            String name = lst.get(i).getKey();
            Long number = lst.get(i).getValue();
            CountryVisitInfo countryVisitInfo = new CountryVisitInfo();
            countryVisitInfo.setName(name);
            countryVisitInfo.setNumber(number + "");
            String geo = COUNTRY_INFO_MAP.containsKey(name) ? (String) COUNTRY_INFO_MAP.get(name)[1] : "";
            countryVisitInfo.setGeo(geo);
            countryVisitInfos.add(countryVisitInfo);
        }
        Long sum = lst.stream().filter((k) -> !churchyardCountry.contains(k.getKey())).map(Map.Entry::getValue).reduce(0L, Long::sum);
        result.setCountryVisitInfos(countryVisitInfos);
        result.setNumOfOverseasVisit(sum + "");
        int numOfOnlineUser = users.size();
        result.setNumOfOnlineUser(numOfOnlineUser + "");
        return result;
    }

    private Set<String> wrapEsResutUser(SearchResponse sr) {
        HashMap<String, String> ipNumberMap = BASE_DAO.getAllEmployeeIpAndNumber();
        Set<String> users = new HashSet<>();
        Terms sourceIPs = sr.getAggregations().get("sourceIP");
        for (Terms.Bucket sourceIP : sourceIPs.getBuckets()) {
            String IP = sourceIP.getKeyAsString();
            String number = ipNumberMap.get(IP);
            if (null != number) {
                users.add(number);
            }
        }
        return users;
    }


    private String matchCountryByIp(String ip) {
        long midIP = NumberUtil.transferIp2Number(ip);
        String country;
        int pos;
        pos = recursionBinarySearch(COUNTRY_KEYS, midIP, 0, COUNTRY_KEYS.size() - 1);
        try {
            Object[] value = COUNTRY_IPS_MAP.get(COUNTRY_KEYS.get(pos));
            country = (long) value[0] > midIP ? (String) value[1] : UNKNOWN_IP_COUNTRY;
        } catch (ArrayIndexOutOfBoundsException e) {
            country = UNKNOWN_IP_COUNTRY;
            e.printStackTrace();
        }
        return country;
    }


    public int recursionBinarySearch(List<Long> lst, long key, int low, int high) {
        if (key < lst.get(low) || key > lst.get(high) || low > high) {
            if (key < lst.get(low) || low > high) {
                return -1;
            } else if (key > lst.get(high)) {
                return high;
            }
        }
        int middle = (low + high) / 2;
        long dymic = lst.get(middle);
        if (dymic > key) {
            long pre = lst.get(middle - 1);
            if (pre < key) {
                return middle - 1;
            }
            return recursionBinarySearch(lst, key, low, middle - 1);
        } else if (dymic < key) {
            long next = lst.get(middle + 1);
            if (next > key) {
                return middle;
            }
            return recursionBinarySearch(lst, key, middle + 1, high);
        } else {
            return middle;
        }
    }

}
