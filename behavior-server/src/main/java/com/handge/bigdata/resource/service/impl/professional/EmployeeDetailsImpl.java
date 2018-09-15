package com.handge.bigdata.resource.service.impl.professional;

import com.handge.bigdata.UnifiedException;
import com.handge.bigdata.dao.ProxyFactory;
import com.handge.bigdata.dao.SQLBuilder;
import com.handge.bigdata.dao.api.IBaseDAO;
import com.handge.bigdata.dao.proxy.EsProxy;
import com.handge.bigdata.dao.proxy.Proxy;
import com.handge.bigdata.enumeration.DAOProxyEnum;
import com.handge.bigdata.enumeration.ESIndexEnum;
import com.handge.bigdata.enumeration.EmployeeStatusEnum;
import com.handge.bigdata.resource.models.request.professional.EmployeeDetailsParam;
import com.handge.bigdata.resource.models.response.professional.EmployeeDetailsInfo;
import com.handge.bigdata.resource.service.api.professional.IEmployeeDetails;
import com.handge.bigdata.utils.DateUtil;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by DaLu Guo on 2018/6/12.
 */
@Component
public class EmployeeDetailsImpl implements IEmployeeDetails {
    /**
     * 基础数据库查询Bean
     */
    @Autowired
    IBaseDAO baseDAO;

    @Override
    public Object getEmployeeDetails(EmployeeDetailsParam employeeDetailsParam) {

        Map<String, Object> configMap = baseDAO.getConfigParam();

        /**
         * 1 查询mysql
         */
        ResultSet resultSet = searchFronmMysql(employeeDetailsParam.getNumber());
        /**
         * 2 查询标签
         */
        List<String> tags = searchFromEs(employeeDetailsParam.getNumber(), configMap);
        /**
         * 3 封装数据返回前端
         */

        EmployeeDetailsInfo employeeDetailsInfo = returnResult(resultSet, tags);

        return employeeDetailsInfo;
    }

    private ResultSet searchFronmMysql(String number) {
        Proxy proxy = ProxyFactory.createProxy(DAOProxyEnum.MySQL);
        ResultSet tagResult = null;
        try {
            String sql = "SELECT\n" +
                    "\te.`name`,\n" +
                    "\te.number,\n" +
                    "\te.`status`,\n" +
                    "\te.seniority,\n" +
                    "\te.post_age,\n" +
                    "\te.post,\n" +
                    "\te.positional_titles,\n" +
                    "\td.department_name\n" +
                    "FROM\n" +
                    "\tentity_employee_information_basic e\n" +
                    "INNER JOIN entity_department_information_basic d ON d.department_id = e.department_id\n" +
                    "WHERE\n" +
                    "\te.number = #{number}";
            String excuteSql = SQLBuilder.sql(sql)
                    .setParamter("number", number)
                    .toString();
            tagResult = (ResultSet) proxy.queryBySQL(excuteSql);
        } catch (Exception e) {
            throw new UnifiedException(e);
        }
        return tagResult;
    }

    private List<String> searchFromEs(String number, Map configMap) {
        List<String> ips = baseDAO.getIpsByNo(number);
        EsProxy esProxy = ProxyFactory.createProxy(DAOProxyEnum.ES);
        TransportClient esClient = esProxy.getClient();
        long startTimeStamp = DateUtil.getTimeByLastYear();
        long endTimeStamp = System.currentTimeMillis();
        String[] indices = esProxy.generateIndices(startTimeStamp, endTimeStamp, ESIndexEnum.MAPPING);
        String esType = configMap.get("ES_TYPE").toString();
        SearchRequestBuilder builder = esClient.prepareSearch(indices)
                .setTypes(esType)
                .setQuery(QueryBuilders.boolQuery()
                        .filter(QueryBuilders.rangeQuery("startTime").from(startTimeStamp, true).to(endTimeStamp, true))
                        .mustNot(QueryBuilders.termsQuery("appName", ""))
                        .filter(QueryBuilders.termsQuery("localIp", ips))
                )
                .addAggregation(AggregationBuilders.terms("appTags").field("appTags").size(10)
                        .subAggregation(AggregationBuilders.count("count").field("_index"))
                );
        SearchResponse response = esProxy.action(builder);
        List<String> tags = new ArrayList<>();
        Terms appTerm = response.getAggregations().get("appTags");
        for (Terms.Bucket bucket1 : appTerm.getBuckets()) {
            tags.add(bucket1.getKeyAsString());
        }
        esProxy.returnClient();
        return tags;
    }

    private EmployeeDetailsInfo returnResult(ResultSet resultSet, List<String> tags) {
        EmployeeDetailsInfo employeeDetailsInfo = new EmployeeDetailsInfo();
        try {
            while (resultSet.next()) {
                employeeDetailsInfo.setName(resultSet.getString("name"));
                employeeDetailsInfo.setDepartment(resultSet.getString("department_name"));
                employeeDetailsInfo.setNumber(resultSet.getString("number"));
                employeeDetailsInfo.setStatus(EmployeeStatusEnum.getDescByStatus(resultSet.getString("status")));
                employeeDetailsInfo.setSeniority(resultSet.getString("seniority"));
                employeeDetailsInfo.setPostAge(resultSet.getString("post_age"));
                employeeDetailsInfo.setPost(resultSet.getString("post"));
                employeeDetailsInfo.setPositionalTitles(resultSet.getString("positional_titles"));
                employeeDetailsInfo.setTags(tags);
            }
        } catch (Exception e) {
            throw new UnifiedException(e);
        }
        return employeeDetailsInfo;
    }
}
