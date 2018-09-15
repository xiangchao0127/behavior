package com.handge.bigdata.dao.proxy;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.ElasticSearchDruidDataSourceFactory;
import com.handge.bigdata.UnifiedException;
import com.handge.bigdata.base.ComponentBaseHandler;
import com.handge.bigdata.enumeration.DateFormatEnum;
import com.handge.bigdata.enumeration.ESIndexEnum;
import com.handge.bigdata.enumeration.ExceptionWrapperEnum;
import com.handge.bigdata.pools.EnvironmentContainer;
import com.handge.bigdata.utils.DateUtil;
import com.handge.bigdata.utils.LogUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;

/**
 * Created by Liujuhao on 2018/4/28.
 */
public class EsProxy extends ComponentBaseHandler implements Proxy {

    private static Date MIN_TIME_LIMIT;
    private static Logger logger = LogManager.getLogger(EsProxy.class);
    private static Properties properties = new Properties();
    private static DruidDataSource dds = null;

    static {
        try {
            MIN_TIME_LIMIT = DateUtil.str2Date(DateFormatEnum.MONTH, "2018-05");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private TransportClient transportClient;

    public EsProxy() {
        try {
            System.setProperty("CONF_DB_USER", "ENV");
            EnvironmentContainer.setENV();
            properties.put("url", "jdbc:elasticsearch://172.20.31.4:9300/");
            dds = (DruidDataSource) ElasticSearchDruidDataSourceFactory.createDataSource(properties);
            dds.setInitialSize(5);
        } catch (Exception e) {
            logger.error(LogUtil.getTrace(e));
            throw new RuntimeException(LogUtil.getTrace(e));
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (transportClient != null) {
            returnClient();
        }
    }

    @Override
    public ResultSet queryBySQL(String sql) {
        DruidPooledConnection connection = null;
        try {
            connection = dds.getConnection();
            return connection.prepareStatement(sql).executeQuery();
        } catch (Exception e) {
            logger.error(LogUtil.getTrace(e));
            throw new RuntimeException(LogUtil.getTrace(e));
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(LogUtil.getTrace(e));
                }
            }
        }
    }

    @Override
    public TransportClient getClient() {
        logger.debug("获取ES客户端");
        transportClient = super.pools.getEsConnection();
        return transportClient;
    }

    @Override
    public SearchResponse action(Object srb) {
        Object result = new WrapTaskUseEsTrspCliProxy<Object>() {
            @Override
            public Object call() throws Exception {
                return ((SearchRequestBuilder) srb).execute().actionGet();
            }
        }.run();
        SearchResponse sr = (SearchResponse) result;
        return sr;
    }

    @Override
    public void returnClient() {
        new WrapReturnConnector<Object>() {
            @Override
            public void run() {
                pools.returnEsConnection(transportClient);
            }
        }.run();
        transportClient = null;
    }

    /**
     * 根据时间范围，获取该范围内的索引名（命名风格：yyyy_MM_enumName）
     * 两个时间点都为开区间，只想获取一个索引时，传入相同时间即可
     *
     * @param [date1, date2, esEnum]
     * @return java.lang.String[]
     * @author LiuJihao
     * @date 2018/5/21 14:27
     **/
    public String[] generateIndices(Object date1, Object date2, ESIndexEnum esEnum) {
        String named_rule = "yyyy_MM";
        Date start = getDateFromObject(date1);
        Date end = getDateFromObject(date2);
        start = start.compareTo(MIN_TIME_LIMIT) <= 0 ? MIN_TIME_LIMIT : start;
        end = end.compareTo(new Date()) >= 0 ? new Date() : end;
        List<String> indices = new LinkedList();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (start.compareTo(end) <= 0) {
            Date currentDate = calendar.getTime();
            String prefix = DateUtil.date2Str(currentDate, named_rule);
            calendar.add(Calendar.MONTH, 1);
            start = calendar.getTime();
            if (ESIndexEnum.ALL.equals(esEnum)) {
                String index1 = prefix + ESIndexEnum.FILTER.getName();
                String index2 = prefix + ESIndexEnum.MAPPING.getName();
                indices.add(index1);
                indices.add(index2);
            } else {
                String index = prefix + esEnum.getName();
                indices.add(index);
            }
        }

        String[] reuslt = indices.toArray(new String[0]);

        return reuslt;
    }

    private Date getDateFromObject(Object o) {
        Date date = null;
        if (o instanceof String) {
            try {
                date = DateUtil.str2Date(DateFormatEnum.MONTH, (String) o);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (o instanceof Date) {
            date = (Date) o;
        } else if (o instanceof Long) {
            date = new Date((Long) o);
        } else {
//            throw new Error("参数类型异常：" + o.getClass().getTypeName() + "，只支持Date类型、String类型和Long类型");
            throw new UnifiedException(o.getClass().getTypeName(), ExceptionWrapperEnum.ClassCastException);
        }
        return date;
    }

}
