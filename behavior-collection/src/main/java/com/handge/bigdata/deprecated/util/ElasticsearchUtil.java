//package com.handge.bigdata.deprecated.util;
//
//import com.handge.bigdata.deprecated.JsonParseImpl;
//import com.handge.bigdata.deprecated.impl.IJsonParse;
//import com.handge.bigdata.pools.Pools;
//import com.handge.bigdata.pools.common.InternalPools;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.client.transport.TransportClient;
//import org.elasticsearch.common.unit.TimeValue;
//import org.elasticsearch.index.query.QueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.slf4j.LoggerFactory;
//
//import java.net.UnknownHostException;
//import java.util.HashMap;
//import java.util.Map;
//
//
//public class ElasticsearchUtil {
//    public final static String[] HOSTS = {"datanode1", "datanode2", "datanode3"};//http请求的端口是9200，客户端是9300
//    public final static int PORT = 9300;//http请求的端口是9200，客户端是9300
//    private org.slf4j.Logger logger = LoggerFactory.getLogger(ElasticsearchUtil.class);
//
//    public static TransportClient getESClient() throws UnknownHostException {
//
////        //创建客户端
////        TransportClient client = new PreBuiltTransportClient(Settings.builder().put("cluster.name", "handge-cloud").build()).addTransportAddresses(
////                new InetSocketTransportAddress(InetAddress.getByName(HOSTS[0]), PORT),
////                new InetSocketTransportAddress(InetAddress.getByName(HOSTS[1]), PORT),
////                new InetSocketTransportAddress(InetAddress.getByName(HOSTS[2]), PORT));
//
//        Pools pools = new InternalPools(ConnectionPropUtil.getParameters());
//        TransportClient esConnection = pools.getEsConnection();
////        IndexResponse response = null;
////        try {
////            response = esConnection.prepareIndex("msg", "tweet", "1").setSource(XContentFactory.jsonBuilder()
////                    .startObject().field("userName", "张三")
////                    .field("sendDate", new Date())
////                    .field("msg", "你好李四")
////                    .endObject()).get();
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////
////        System.out.println("索引名称:" + response.getIndex() + "\n类型:" + response.getType()
////                + "\n文档ID:" + response.getId() + "\n当前实例状态:" + response.status());
////
////        //关闭客户端
////        pools.returnEsConnection(esConnection);
//        IJsonParse jsonParse = new JsonParseImpl();
//        jsonParse.esToHbase();
//        return esConnection;
//    }
//
//    public static void main(String[] args) throws Exception {
//        getESClient();
//    }
//
//    public static Map getParameters() {
//        Map paramters = new HashMap();
//        paramters.put("hbase.zookeeper.quorum", "datanode1,datanode2,datanode3");
//        paramters.put("hbase.zookeeper.property.clientPort", "2181");
//        paramters.put("zookeeper.znode.parent", "/hbase-unsecure");
//
//        paramters.put("cluster.name", "handge-cloud");
//        paramters.put("es.url", "datanode1:9300,datanode2:9300,datanode3:9300");
//
//        paramters.put("mysql.driver", "com.mysql.jdbc.Driver");
//        paramters.put("mysql.jdbc.url", "jdbc:mysql://172.20.31.108:3306/information?useUnicode=true&characterEncoding=utf8");
//        paramters.put("mysql.user.name", "mysql");
//        paramters.put("mysql.user.password", "mysql");
//
//        paramters.put("bootstrap.servers", "datanode1:6667,datanode2:6667,datanode3:6667");
//        paramters.put("producer.type", "async");
//        paramters.put("key.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
//        paramters.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
//        paramters.put("batch.num.messages", "1000");
//        paramters.put("max.request.size", "1000973460");
//        paramters.put("enable.auto.commit", "true");
//        paramters.put("auto.offset.reset", "latest");
//
//        paramters.put("redis.host", "172.20.31.4");
//        paramters.put("redis.port", "6380");
//        return paramters;
//    }
//
//    public static void getTrace() throws Exception {
//        TransportClient esClient = getESClient();
//        QueryBuilder builders = QueryBuilders.rangeQuery("startTime").gte("1517414413000").lte("1518515413000");
//        System.out.println(builders);
//
//        SearchResponse response = esClient.prepareSearch("dataflow1")
//                .setFrom(0).setSize(100)
//                .setTimeout(TimeValue.timeValueMillis(300))
//                .setFetchSource(false)
//                .setQuery(builders)
//                .setTypes("tweet")
//                .execute().actionGet();
//        System.out.println(response.toString());
//    }
//}
//
//
