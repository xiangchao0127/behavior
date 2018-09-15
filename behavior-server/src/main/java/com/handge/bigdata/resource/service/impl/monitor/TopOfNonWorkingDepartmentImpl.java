package com.handge.bigdata.resource.service.impl.monitor;

import com.handge.bigdata.UnifiedException;
import com.handge.bigdata.dao.ProxyFactory;
import com.handge.bigdata.dao.api.IBaseDAO;
import com.handge.bigdata.dao.proxy.EsProxy;
import com.handge.bigdata.dao.proxy.RedisProxy;
import com.handge.bigdata.enumeration.DAOProxyEnum;
import com.handge.bigdata.enumeration.DateFormatEnum;
import com.handge.bigdata.enumeration.ESIndexEnum;
import com.handge.bigdata.enumeration.ExceptionWrapperEnum;
import com.handge.bigdata.resource.models.UserContext;
import com.handge.bigdata.resource.models.request.monitor.NonWorkingDepartmentDetailParam;
import com.handge.bigdata.resource.models.request.monitor.TopOfNonWorkingDepartmentParam;
import com.handge.bigdata.resource.models.response.monitor.NonWorkingDepartmentTop;
import com.handge.bigdata.resource.service.api.monitor.ITopOfNonWorkingDepartment;
import com.handge.bigdata.utils.CollectionUtils;
import com.handge.bigdata.utils.DateUtil;
import com.handge.bigdata.utils.PageResults;
import com.handge.bigdata.utils.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.max.InternalMax;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/***
 *
 * @author MaJianfu
 * @date 2018/5/21 10:49
 **/
@Component
public class TopOfNonWorkingDepartmentImpl implements ITopOfNonWorkingDepartment {

    @Autowired
    IBaseDAO baseDao;
    /**
     * 非工作标志
     */
    private static final String NO_WORK_CODE = "0";

    @Override
    public Object listTopOfNonWorkingDepartment(TopOfNonWorkingDepartmentParam topOfNonWorkingDepartmentParam) {
        List<NonWorkingDepartmentTop> listTopOfNonWorkingDepartment = listTopOfNonWorkingDepartmentResult(null);
        List<NonWorkingDepartmentTop> listNonWorkingDepartmentTop = listNonWorkingDepartmentTop(topOfNonWorkingDepartmentParam.getN(), listTopOfNonWorkingDepartment);
        return listNonWorkingDepartmentTop.subList(0, listNonWorkingDepartmentTop.size() > topOfNonWorkingDepartmentParam.getN() ? topOfNonWorkingDepartmentParam.getN() : listNonWorkingDepartmentTop.size());
    }

    @Override
    public Object listNonWorkingDepartmentDetail(NonWorkingDepartmentDetailParam nonWorkingDepartmentDetailParam) {
        List<NonWorkingDepartmentTop> listTopOfNonWorkingDepartment = listTopOfNonWorkingDepartmentResult(nonWorkingDepartmentDetailParam.getDepartment());
        PageResults<NonWorkingDepartmentTop> pageResult = CollectionUtils.getPageResult(listTopOfNonWorkingDepartment, nonWorkingDepartmentDetailParam.getPageNo(), nonWorkingDepartmentDetailParam.getPageSize());
        return pageResult;
    }

    private List<NonWorkingDepartmentTop> listResult(UserContext userContext, String departmentName) {

            HashMap<String, Integer> departmentMap = baseDao.numberOfEmployeesGroupByDep();
            HashMap<String, ArrayList<String>> departmentDeviceIpMap = baseDao.getEmployeeIps(departmentName);
            RedisProxy pool = ProxyFactory.createProxy(DAOProxyEnum.Redis);
            Jedis jedis = pool.getConnection();
            String stampKey = DateUtil.date2Str(new Date(), DateFormatEnum.DAY);
            Set<String> hkeys = jedis.hkeys(stampKey);
            List<NonWorkingDepartmentTop> result = new ArrayList<>();
            double v = 0.0;
            HashMap<String, String> map = baseDao.getAllEmployeeIpAndNumber();
            for (Map.Entry<String, ArrayList<String>> entry : departmentDeviceIpMap.entrySet()) {
                List<String> ipList = new ArrayList<>();
                //获取部门的所有ip
                List<String> ips = entry.getValue();
                for (String key : hkeys) {
                    if (ips.contains(key)) {
                        String value = jedis.hget(stampKey, key);
                        ipList.add(key + "|" + value);
                    }
                }
                Set set = new HashSet();
                for (String list : ipList) {
                    String[] split = list.split("\\|");
                    if(map.containsKey(split[0])){
                        set.add(map.get(split[0]));
                    }
                }
                int departNum = set.size();
                int departmentNum = departmentMap.get(entry.getKey());
                v = Double.parseDouble(departNum + ".0") / departmentNum;
                DecimalFormat dFormat = new DecimalFormat("#0.0");
                NonWorkingDepartmentTop nonWorkingDepartmentTop = new NonWorkingDepartmentTop();
                nonWorkingDepartmentTop.setDepartment(entry.getKey());
                nonWorkingDepartmentTop.setNumOfNonWorking(String.valueOf(departNum));
                nonWorkingDepartmentTop.setNumOfPerson(String.valueOf(departmentNum));
                nonWorkingDepartmentTop.setRatioOfNonWorkingDepartment(dFormat.format(v * 100).toString());
                result.add(nonWorkingDepartmentTop);
            }
            pool.returnRedis(jedis);
            // 通过比较器来实现排序
            Collections.sort(result, new Comparator<NonWorkingDepartmentTop>() {
                @Override
                public int compare(NonWorkingDepartmentTop o1, NonWorkingDepartmentTop o2) {
                    return -Double.compare(Double.parseDouble(o1.getRatioOfNonWorkingDepartment()),
                            Double.parseDouble(o2.getRatioOfNonWorkingDepartment()));
                }
            });
            return result;
    }

    private List<NonWorkingDepartmentTop> listNonWorkingDepartmentTop(int n, List<NonWorkingDepartmentTop> list) {
        int i = 0;
        List<NonWorkingDepartmentTop> list2 = new LinkedList<>();
        for (NonWorkingDepartmentTop mapping : list) {
            list2.add(mapping);
            i++;
            if (i == n) {
                break;
            }
        }
        return list2;
    }

    private HashMap<String, String> hashMapNoWork(String departmentName){
        //定义生效时间
        Map<String, Object> configParam = baseDao.getConfigParam();
        String startTime = configParam.get("QUERY_TIME").toString();
        Long startTimeMills = System.currentTimeMillis() - (new Double(Double.parseDouble(startTime) * 60000).longValue());
        Long nowTimeMills = System.currentTimeMillis();
        EsProxy esProxy = ProxyFactory.createProxy(DAOProxyEnum.ES);
        TransportClient esClient = esProxy.getClient();
        String today = DateUtil.timeStampToStrDate(System.currentTimeMillis(), DateFormatEnum.DAY);
        String[] indices = esProxy.generateIndices(today, today, ESIndexEnum.MAPPING);
        String esType = configParam.get("ES_TYPE").toString();
        BoolQueryBuilder builder = QueryBuilders.boolQuery().filter(QueryBuilders.rangeQuery("startTime").gte(startTimeMills).lte(nowTimeMills));
        if (StringUtils.notEmpty(departmentName)) {
            if( baseDao.getEmployeeIps(departmentName).size()==0){
                throw new UnifiedException("部门名称不存在 ", ExceptionWrapperEnum.IllegalArgumentException);
            }
            builder.filter(QueryBuilders.termsQuery("localIp", baseDao.getEmployeeIps(departmentName).get(departmentName)));
        }
        SearchRequestBuilder searchRequestBuilder = esClient.prepareSearch(indices)
                .setTypes(esType).setQuery(builder)
                .addAggregation(AggregationBuilders.terms("sourceIP").field("localIp").size(10000)
                        .subAggregation(AggregationBuilders.max("startTime").field("startTime")).size(10000)).setSize(10000);
        SearchResponse sr = esProxy.action(searchRequestBuilder);
        Terms sourceIP = sr.getAggregations().get("sourceIP");
        //获取员工IP与编号
        HashMap<String, String> allEmployeeIpAndNumber = baseDao.getAllEmployeeIpAndNumber();
        //获取基础标签与对应的抽象标签
        HashMap<String, String> jobClass = baseDao.getJobClass();
        //定义非工作类
        HashMap<String, String> hashMapNoWork = new HashMap<>(16);
        for (Terms.Bucket bucket : sourceIP.getBuckets()) {
            String ip = bucket.getKeyAsString();
            double time = ((InternalMax) bucket.getAggregations().get("startTime")).getValue();
            if (allEmployeeIpAndNumber.containsKey(ip)) {
                SearchRequestBuilder requestBuilderSub = esClient.prepareSearch(indices).setTypes("data")
                        .setQuery(QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("localIp", ip))
                                .must(QueryBuilders.termsQuery("startTime", new BigDecimal(time).toString())))
                        .addAggregation(AggregationBuilders.terms("app_tag_collection").field("appTags").size(10000).order(Terms.Order.count(false)).size(1)
                        );
                SearchResponse sp = esProxy.action(requestBuilderSub);
                Terms app = sp.getAggregations().get("app_tag_collection");
                for (Terms.Bucket bucketSub : app.getBuckets()) {
                    String appClass = bucketSub.getKeyAsString();
                    String appClassStr = jobClass.get(appClass);
                    if (NO_WORK_CODE.equals(appClassStr)) {
                        hashMapNoWork.put(allEmployeeIpAndNumber.get(ip), ip);
                    }
                }
            }
        }
        esProxy.returnClient();
        return hashMapNoWork;
    }

    private List<NonWorkingDepartmentTop> listTopOfNonWorkingDepartmentResult(String departmentName){
        //查询各个部门人数(在岗)  (部门名称  人数)
        HashMap<String, Integer> departmentMap = baseDao.numberOfEmployeesGroupByDep();
        //获取部门所有ip  (部门名称  list(ip))
        HashMap<String, ArrayList<String>> departmentDeviceIpMap = baseDao.getEmployeeIps(departmentName);
        //工作无关  (工号    ip)
        HashMap<String, String> hashMapNoWork = hashMapNoWork(departmentName);
        List<NonWorkingDepartmentTop> result = new LinkedList<>();
        DecimalFormat dFormat = new DecimalFormat("#0.0");
        for (Map.Entry<String, ArrayList<String>> entry : departmentDeviceIpMap.entrySet()) {
            int i=0;
            for (Map.Entry<String,String> mapping : hashMapNoWork.entrySet()) {
                ArrayList<String> depList = entry.getValue();
                if(depList.contains(mapping.getValue())){
                    i++;
                }
            }
            int departNum = i;
            int departmentNum = departmentMap.get(entry.getKey());
            double v = Double.parseDouble(departNum + ".0") / departmentNum;
            NonWorkingDepartmentTop nonWorkingDepartmentTop = new NonWorkingDepartmentTop();
            nonWorkingDepartmentTop.setDepartment(entry.getKey());
            nonWorkingDepartmentTop.setNumOfNonWorking(String.valueOf(departNum));
            nonWorkingDepartmentTop.setNumOfPerson(String.valueOf(departmentNum));
            nonWorkingDepartmentTop.setRatioOfNonWorkingDepartment(dFormat.format(v * 100).toString());
            result.add(nonWorkingDepartmentTop);
        }
            // 通过比较器来实现排序
            Collections.sort(result, new Comparator<NonWorkingDepartmentTop>() {
                @Override
                public int compare(NonWorkingDepartmentTop o1, NonWorkingDepartmentTop o2) {
                    return -Double.compare(Double.parseDouble(o1.getRatioOfNonWorkingDepartment()),
                            Double.parseDouble(o2.getRatioOfNonWorkingDepartment()));
                }
            });
        return result;
    }
}
