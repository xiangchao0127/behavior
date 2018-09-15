/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.handge.bigdata.driver

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, ConsumerStrategy, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Duration, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}


trait StreamingDriver extends Serializable {

  def initSparkConf: SparkConf

  def getKafkaParms: Map[String, String]


  def getTopic: Array[String]

  def getDuration: Duration

  def handleStreaming(streamingValue: DStream[Array[Byte]])

  def runStreaming() = {
    val sparkConf = initSparkConf
    run(sparkConf)
  }


  def runStreaming(masterMode: String): Unit = {
    val sparkConf = initSparkConf.setMaster(masterMode)
    run(sparkConf)
  }

  def run(sparkConf: SparkConf): Unit = {
    val sparkContext = SparkContext.getOrCreate(sparkConf)
    val sparkStreamContext = createSparkStreamingContex(sparkContext)
    sparkStreamContext.start()
    sparkStreamContext.awaitTermination()
  }

  def createSparkStreamingContex(sparkContext: SparkContext): StreamingContext = {

    val streamingContext = StreamingContext.getActiveOrCreate(() => new StreamingContext(sparkContext, getDuration))

    val consumerStrategy: ConsumerStrategy[Array[Byte], Array[Byte]] = ConsumerStrategies.Subscribe[Array[Byte], Array[Byte]](getTopic, getKafkaParms)

    val dStream: InputDStream[ConsumerRecord[Array[Byte], Array[Byte]]] = KafkaUtils.createDirectStream[Array[Byte], Array[Byte]](
      streamingContext,
      LocationStrategies.PreferConsistent,
      consumerStrategy)
    val streamingValue = dStream.map(r => r.value())

    handleStreaming(streamingValue)

    streamingContext
  }

}


