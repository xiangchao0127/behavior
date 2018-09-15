/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.handge.bigdata.driver

import com.handge.bigdata.handlerchain.{BaseContext, HandlerChain}
import org.apache.commons.configuration2.Configuration
import org.apache.spark.SparkConf
import org.apache.spark.streaming.Duration
import org.apache.spark.streaming.dstream.DStream

trait BootStrap extends StreamingDriver {

  override def initSparkConf: SparkConf = {
    val sparkConf = new SparkConf().setAppName(getAppName)
    val region_prefix = getAppName + "." + Regions.spark_conf_region
    val keys = getConfiguration.getKeys(region_prefix)
    do {
      val key = keys.next()
      val prefix = region_prefix + "."
      sparkConf.set(key.replace(prefix, "").trim, getConfiguration.getString(key))
    } while (keys.hasNext)

    sparkConf
  }

  override def getKafkaParms: Map[String, String] = {
    var map: Map[String, String] = Map()
    val region_prefix = getAppName + "." + Regions.kafka_consumer_region
    val keys = getConfiguration.getKeys(region_prefix)
    do {
      val key = keys.next()
      val prefix = region_prefix + "."
      map += (key.replace(prefix, "").trim -> getConfiguration.getString(key))
    } while (keys.hasNext)

    map
  }

  override def getTopic: Array[String] = {
    val key = getAppName + ".topics"
    val topics = getConfiguration.getStringArray(key)

    println(topics.mkString)
    topics

  }

  override def getDuration: Duration = {
    Duration.apply(2000)
  }

  override def handleStreaming(streamings: DStream[Array[Byte]]): Unit = {
    streamings.foreachRDD((rdd, time) => {
      rdd.foreachPartition(datas => {
        val context = new BaseContext[Iterator[Array[Byte]]]()
        context.setContext(datas)
        getHandlerChain.execute(context)
      })
    })
  }


  def getAppName: String

  def getConfiguration: Configuration

  def getHandlerChain: HandlerChain
}
