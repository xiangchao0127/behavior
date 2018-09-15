package com.handge.bigdata.serivice

import java.text.SimpleDateFormat
import java.util
import java.util.concurrent.Executors


import com.handge.bigdata.handers.DbUitl
import com.handge.bigdata.utils.StringUtils
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.client.{ConnectionFactory, Put, Table}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.log4j.Logger
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SparkSession, functions}
import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.bulk.BulkResponse

import scala.collection.JavaConversions.seqAsJavaList

class JsonParseImpl extends Serializable {

  val simpletamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  val simple = new SimpleDateFormat("yyyy-MM-dd HH")
  val simpledat = new SimpleDateFormat("yyyyMMddHH")

  //(小时,ip,分类,计数|时间)
  def parseDataToHBase(data: RDD[TagsData]): Unit = {
    try {
      data.map(value => {
        val put = new Put(Bytes.toBytes(simpletamp.parse(value.score.split("\\|")(1)).getTime.toString.substring(9, 10)
          + "|" + simpledat.format(simple.parse(value.time)) + "_" + value.userName))
        put.addColumn("info".getBytes, value.tags.getBytes, value.score.split("\\|")(0).getBytes)
        put
      }).foreachPartition(iterator => {
        val config = HBaseConfiguration.create()
        val connection = ConnectionFactory.createConnection(config)
        val table: Table = connection.getTable(TableName.valueOf("xc1"))
        table.put(seqAsJavaList(iterator.toSeq))
        connection.close()
      })
    } catch {
      case e: Exception => throw new Exception(e)
    }
  }

  /**
    * 将数据写入Hbase
    *
    */
  def toHbase(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("jsonParse").master("local[3]")
      .config("spark.sql.warehouse.dir", "D://test")
      .config("spark.testing.memory", "2147480000") //超过512M
      .getOrCreate()

    //    val df = spark.read.json(args(0)) //读取hdfs文件  参数自定义
//    val df = spark.read.json("hdfs://master-1:8020/sangfor")
         val df = spark.read.json("hdfs://172.18.50.46:9000/home/data/input")
    val dfData = df.select(functions.explode(df("data"))).toDF("data") //获取json data节点
    val hashMapSign = DbUitl.getAllClassName(DbUitl.getConnection) //获取数据库标签放入内存 防止连接过多
    val rdd = dfData.select("data.date_time", "data.hst_ip", "data.serv_name", "data.app_name").rdd.filter(r => StringUtils.isInnerIp(r.get(1).toString))
    rddHandle(rdd, hashMapSign)
  }

  /**
    * rdd转换
    *
    * @param rdd
    * @param hashMapSign
    */
  def rddHandle(rdd: org.apache.spark.rdd.RDD[Row], hashMapSign: util.HashMap[String, String]): Unit = {
    val dsData = rdd.map(
      r => { //为用户数据添加标签
        (r.get(0).toString.substring(0, 13), r.get(1).toString, hashMapSign.get(r.get(3)), 1, r.get(0).toString)
      }).filter(r => r._3 != "" && r._3 != null) //去除没有标签的数据
    groupRdd(dsData)
  }

  def groupRdd(dsData: org.apache.spark.rdd.RDD[(String, String, String, Int, String)]): Unit = {
    val dsDataNew = dsData.flatMap(r => { //将多类标签拆分
      val r3 = r._3.split("/")
      for (i <- 0 until r3.length) yield (r._1, r._2, r3(i), r._4, r._5) //(小时,ip,标签,1,时间)
    })


    val dsByIpAndTimeAndClass = dsDataNew.map( //根据ip 小时 分类 做三次分组
      r => (r._2, r)
    ).groupByKey().map(
      iter => iter._2.groupBy(_._1).map(
        it => (iter._1, it._1, it._2.map(r => (r._3, (r._4, r._5)))) //ip,小时,（标签,(1,时间)）
      ).map(
        r3 => r3._3.groupBy(_._1).map(
          it5 => (iter._1, r3._2, it5._2) //ip 小时 (标签,(1,时间))
        )
      ))
    dsByIpAndTimeAndClass.foreach(println)
    val a = dsByIpAndTimeAndClass.flatMap(r => {
      //分类求和
      r.flatMap(r1 => {
        r1.flatMap(r2 => r2._3.map(r3 => ((r3._1, r2._1, r2._2), r3._2))) //(标签ip小时),(1,时间)
      })
    })

    val da = a.reduceByKey((x, y) => { //根据(标签ip小时)计数
      (x._1 + y._1, x._2)
    }).map(r => {
      val par = TagsData(r._1._3, r._1._2.toString, r._1._1, r._2._1 + "|" + r._2._2) //(小时,ip,分类,计数|时间)
      par
    })
//    log.info("------" + da.partitioner.size)
//    println("------" + da.partitioner.size)
    parseDataToHBase(da) //插入hbase

  }


}

object JsonParseImpl extends Serializable {
  val log = Logger.getLogger(JsonParseImpl.getClass)
  val excutorpools = Executors.newWorkStealingPool(40)
  val esListener = new ActionListener[BulkResponse]() {
    override def onFailure(e: Exception): Unit = {
      println("ES failure ==>" + e.toString)
    }

    override def onResponse(response: BulkResponse): Unit = {
      log.info(response.toString)
    }
  }

  def main(args: Array[String]): Unit = {
    val jsonToEs = new JsonParseImpl()
    jsonToEs.toHbase(args)
  }


}

case class Model(hst_ip: String, app_name: String, dst_ip: String, date_time: String) extends Serializable

case class TagsData(time: String, userName: String, tags: String, score: String) extends Serializable



