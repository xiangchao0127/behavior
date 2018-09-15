///*
// * Copyright (c) 2018  Inc. All rights reserved
// * Projects : net-behavior
// * Module : behavior-collection
// * Class : JsonParseImpl.scala
// * User : XueFei Wang
// * Date : 5/30/18 2:38 PM
// * Modified :4/28/18 3:04 PM
// * Todo :
// *
// */
//
//package com.handge.bigdata.deprecated
//
//import java.text.SimpleDateFormat
//import java.util.{HashMap, UUID}
//import java.util.concurrent.Executors
//import java.util.regex.Pattern
//
//import com.handge.bigdata.common.Pools
//import com.handge.bigdata.deprecated.impl.IJsonParse
//import com.handge.bigdata.deprecated.util.DbUitl
//import com.handge.bigdata.utils.TimeUtils
//import org.apache.hadoop.hbase.client.{Put, Result, Table}
//import org.apache.hadoop.hbase.io.ImmutableBytesWritable
//import org.apache.hadoop.hbase.mapreduce.TableInputFormat
//import org.apache.hadoop.hbase.util.Bytes
//import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
//import org.apache.spark.rdd.RDD
//import org.apache.spark.sql.{Row, SQLContext, SparkSession, functions}
//import org.apache.spark.{SparkConf, SparkContext}
//import org.elasticsearch.action.ActionListener
//import org.elasticsearch.action.bulk.BulkResponse
//import org.elasticsearch.action.index.IndexRequest
//import org.elasticsearch.spark.rdd.EsSpark
//
//import scala.collection.JavaConversions.seqAsJavaList
//
//class JsonParseImpl extends IJsonParse with Serializable {
//  //  val log = Logger.getLogger(JsonParseImpl.getClass)
//
//  val simpletamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//  val simple = new SimpleDateFormat("yyyy-MM-dd HH")
//  val simpledat = new SimpleDateFormat("yyyyMMddHH")
//
//  def toEs(index: String): Unit = {
//    val spark = SparkSession
//      .builder()
//      .appName("jsonParse").config("spark.sql.warehouse.dir", "D:\\test").master("local[3]")
//      .config("spark.testing.memory", "2147480000") //超过512M
//      .getOrCreate()
//    //      .master("local[3]").config("spark.sql.warehouse.dir", "D:\\test")
//    //    val df = spark.read.json("src/main/resources/decode02-10.json") //读取hdfs文件  参数自定义
//    //    val df = spark.read.json(args(0))
//    val df = spark.read.json("hdfs://master-1:8020/sangfor/")
//    //    val df = spark.read.json("hdfs://172.18.50.46:9000/home/data/input")
//    val dfData = df.select(functions.explode(df("data"))).toDF("data") //获取json data节点
//    val hashMapSign = DbUitl.getAllBasicClass(JsonParseImpl.getMysqlConn) //获取数据库标签放入内存 防止连接过多
//    //todo 查询标签映射
//    val dsData = dfData.select("data.hst_ip", "data.app_name", "data.dst_ip", "data.date_time").rdd.map(r => {
//      Model(r.get(0).toString, r.get(1).toString, r.get(2).toString, r.get(3).toString)
//    })
//
//    //源ip 应用名称 应用类别 应用协议 目的IP 访问时间 应用标签集合
//    dsData.foreachPartition(r => {
//      val esClient = JsonParseImpl.getEsClient
//      val bulkRequestBuilder = esClient.prepareBulk()
//      r.foreach(r => {
//        if (hashMapSign.get(r.app_name) != null) {
//          bulkRequestBuilder.add(getIndexRequests(r, index, hashMapSign))
//        }
//      })
//      JsonParseImpl.excutorpools.submit(new Runnable {
//        override def run(): Unit = {
//          bulkRequestBuilder.execute(JsonParseImpl.esListener)
//          //          bulkRequestBuilder.execute()
//          JsonParseImpl.returnEsConn(esClient)
//        }
//      })
//    })
//  }
//
//  def getIndexRequests(item: Model, index: String, signHashMap: HashMap[String, String]): IndexRequest = {
//    val indexRequest = new IndexRequest(index, "tweet")
//    val data = new HashMap[String, Any]()
//    println(item.hst_ip)
//    data.put("sourceIP", item.hst_ip)
//    data.put("appName", item.app_name)
//    data.put("app_tag_collection", signHashMap.get(item.app_name))
//    data.put("destinationIP", item.dst_ip)
//    data.put("startTime", TimeUtils.convertDateStr2TimeStamp(item.date_time, TimeUtils.SECOND_DATE_FORMAT))
//    data.put("appProtocol", "")
//    data.put("upstreamFlow", "")
//    data.put("deviceID", "")
//    data.put("appType", "")
//    data.put("host", "")
//    data.put("details", "")
//    data.put("endTime", "")
//    data.put("id", UUID.randomUUID().toString().replaceAll("-", ""))
//    data.put("downstreamFlow", "")
//    indexRequest.source(data)
//    indexRequest
//  }
//
//  /**
//    * 将数据写入Hbase
//    *
//    */
//  override def toHbase(args: Array[String]): Unit = {
//    val sparkConf = new SparkConf().setMaster("local[3]").setAppName("HBaseTest").set("spark.sql.warehouse.dir", "D:\\test")
//    val hBaseConf = HBaseConfiguration.create()
//    hBaseConf.set(TableInputFormat.INPUT_TABLE, "behaviorBase")
//    val hashMapSign = DbUitl.getAbstractMapping(JsonParseImpl.getMysqlConn)
//    val sc = new SparkContext(sparkConf)
//    val sqlContext = new SQLContext(sc)
//    import sqlContext.implicits._
//    val hbaseRDD = sc.newAPIHadoopRDD(hBaseConf, classOf[TableInputFormat], classOf[ImmutableBytesWritable], classOf[Result])
//
//    val dataFrame1 = hbaseRDD.map(r => (
//      Bytes.toString(r._2.getRow),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("QQ")))),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("邮件")))),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("微博")))),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("论坛")))),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("游戏")))),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("IM")))),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("IM传文件")))),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("社交网络")))),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("网络存储")))),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("下载工具")))),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("P2P")))),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("音乐")))),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("视频")))),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("FTP")))),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("金融")))),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("办公OA")))),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("数据库")))),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("网络会议"))))
//    )).toDF("RowKey", "QQ", "邮件", "微博", "论坛", "游戏", "IM", "IM传文件", "社交网络", "网络存储", "下载工具", "P2P", "音乐", "视频", "FTP", "金融", "办公OA", "数据库", "网络会议")
//
//    val dataFrame2 = hbaseRDD.map(r => (
//      Bytes.toString(r._2.getRow),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("远程登陆")))),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("代理工具")))),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("木马控制")))),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("软件更新")))),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("网上银行")))),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("购物")))),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("综合服务")))),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("阅读图书")))),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("地图导航")))),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("旅行")))),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("气象")))),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("新闻")))),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("科技")))),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("招聘")))),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("IT相关")))),
//      getOrElse(Bytes.toString(r._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("微信"))))
//    )).toDF("RowKey", "远程登陆", "代理工具", "木马控制", "软件更新", "网上银行", "购物", "综合服务", "阅读图书", "地图导航", "旅行", "气象", "新闻", "科技", "招聘", "IT相关", "微信")
//
//    val dataFrame = dataFrame1.join(dataFrame2, "RowKey")
//
//    //    dataFrame.show(false)
//
//    dataFrame.map(r => {
//      (r.getAs[String]("RowKey"), r.getAs[Int]("QQ") + r.getAs[Int]("代理工具"), r.getAs[Int]("购物") + r.getAs[Int]("综合服务"))
//    }).toDF().show(false)
//
//  }
//
//  def getOrElse(str: String): Int = {
//    if (str == null)
//      0
//    else
//      str.toInt
//  }
//
//  /**
//    * rdd转换
//    *
//    * @param rdd
//    * @param hashMapSign
//    */
//  def rddHandle(rdd: org.apache.spark.rdd.RDD[Row], hashMapSign: HashMap[String, String]): Unit = {
//    val dsData = rdd.map(
//      r => { //为用户数据添加标签
//        (r.get(0).toString.substring(0, 13), r.get(1).toString, hashMapSign.get(r.get(3)), 1, r.get(0).toString)
//      }).filter(r => r._3 != "" && r._3 != null) //去除没有标签的数据
//    groupRdd(dsData)
//  }
//
//  /**
//    * 读取ES数据处理后发往hbase
//    */
//  override def esToHbase(): Unit = {
//    val sparkConf = new SparkConf().setAppName("DecisionTree1").setMaster("local[3]")
//    sparkConf.set("es.index.auto.create", "true")
//    sparkConf.set("es.nodes", "datanode1,datanode2,datanode3")
//    sparkConf.set("es.port", "9200")
//    sparkConf.set("cluster.name", "handge-cloud")
//    val sc = new SparkContext(sparkConf)
//    val rdd = EsSpark.esRDD(sc, "dataflow1/tweet")
//    val hashMapSign = DbUitl.getMappingClass(JsonParseImpl.getMysqlConn) //获取数据库标签放入内存 防止连接过多
//    //    (小时,ip,标签,1,时间)
//    val rddMap = rdd.map(r => {
//      val startTime = TimeUtils.convertTimeStamp2DateStr(r._2.get("startTime").get.toString.toLong, TimeUtils.SECOND_DATE_FORMAT)
//      if (JsonParseImpl.pattern.matcher(r._2.get("sourceIP").get.toString).matches()) {
//        (startTime.substring(0, 13), r._2.get("sourceIP").get.toString, r._2.get("app_tag_collection").get.toString, 1, startTime)
//      } else {
//        (startTime.substring(0, 13), r._2.get("destinationIP").get.toString, r._2.get("app_tag_collection").get.toString, 1, startTime)
//      }
//    })
//    val rddFlat = rddMap.flatMap(r => {
//      val r3 = r._3.split("/")
//      for (i <- 0 until r3.length) yield (r._1, r._2, hashMapSign.get(r3(i)), r._4, r._5)
//      //      for (i <- 0 until r3.length) yield (r._1, r._2, r3(i), r._4, r._5)
//    }).filter(r => r._3 != null)
//    //    rddFlat.foreach(println)
//    groupRdd(rddFlat)
//  }
//
//  def groupRdd(dsData: org.apache.spark.rdd.RDD[(String, String, String, Int, String)]): Unit = {
//    val dsDataNew = dsData.flatMap(r => { //将多类标签拆分
//      val r3 = r._3.split("/")
//      for (i <- 0 until r3.length) yield (r._1, r._2, r3(i), r._4, r._5) //(小时,ip,标签,1,时间)
//    })
//    val dsByIpAndTimeAndClass = dsDataNew.map( //根据ip 小时 分类 做三次分组
//      r => (r._2, r)
//    ).groupByKey().map(
//      iter => iter._2.groupBy(_._1).map(
//        it => (iter._1, it._1, it._2.map(r => (r._3, (r._4, r._5)))) //ip,小时,（标签,(1,时间)）
//      ).map(
//        r3 => r3._3.groupBy(_._1).map(
//          it5 => (iter._1, r3._2, it5._2) //ip 小时 (标签,(1,时间))
//        )
//      ))
//    val a = dsByIpAndTimeAndClass.flatMap(r => {
//      //分类求和
//      r.flatMap(r1 => {
//        r1.flatMap(r2 => r2._3.map(r3 => ((r3._1, r2._1, r2._2), r3._2))) //(标签ip小时),(1,时间)
//      })
//    })
//
//    val da = a.reduceByKey((x, y) => { //根据(标签ip小时)计数
//      (x._1 + y._1, x._2)
//    }).map(r => {
//      val par = TagsData(r._1._3, r._1._2.toString, r._1._1, r._2._1 + "|" + r._2._2) //(小时,ip,分类,计数|时间)
//      par
//    })
//    //    log.info("------" + da.partitioner.size)
//    //    println("------" + da.partitioner.size)
//    parseDataToHBase(da) //插入hbase
//
//  }
//
//  def parseDataToHBase(data: RDD[TagsData]): Unit = {
//    try {
//      val put = data.map(value => {
//        val put = new Put(Bytes.toBytes(simpletamp.parse(value.score.split("\\|")(1).toString).getTime.toString.substring(9, 10)
//          + "|" + simpledat.format(simple.parse(value.time)) + "_" + value.userName))
//        put.addColumn("info".getBytes, value.tags.getBytes, value.score.split("\\|")(0).getBytes)
//        put
//      })
//      put.foreachPartition(iterator => {
//        val hbaseConnection = JsonParseImpl.getHbaseConn
//        val table: Table = hbaseConnection.getTable(TableName.valueOf("behaviorAbstract1"))
//        iterator.foreach(r => {
//          table.put(seqAsJavaList(iterator.toSeq))
//        })
//        JsonParseImpl.returnHbaseConn(hbaseConnection)
//      })
//    } catch {
//      case e: Exception => throw new Exception(e)
//    }
//  }
//
//
//}
//
//object JsonParseImpl extends Serializable with Pools {
//
//  val ALLOWABLE_IP_REGEX = "(127[.]0[.]0[.]1)|" + "(localhost)|" + "(^10(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){3}$)|" + "(^172\\.([1][6-9]|[2]\\d|3[01])(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){2}$)|" + "(^192\\.168(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){2}$)"
//  val pattern = Pattern.compile(JsonParseImpl.ALLOWABLE_IP_REGEX)
//  val excutorpools = Executors.newWorkStealingPool(40)
//  val esListener = new ActionListener[BulkResponse]() {
//    override def onFailure(e: Exception): Unit = {
//      println("ES failure ==>" + e.toString)
//    }
//
//    override def onResponse(response: BulkResponse): Unit = {
//      log.info(response.toString)
//    }
//  }
//
//  def main(args: Array[String]): Unit = {
//    val jsonToEs = new JsonParseImpl()
//    //    if (args(0).equals("0")) {
//    //      jsonToEs.toEs(args(1))
//    //    } else {
//    //      jsonToEs.esToHbase()
//    //    }
//    jsonToEs.toHbase(args)
//  }
//}
//
//case class Model(hst_ip: String, app_name: String, dst_ip: String, date_time: String) extends Serializable
//
//case class TagsData(time: String, userName: String, tags: String, score: String) extends Serializable
//
