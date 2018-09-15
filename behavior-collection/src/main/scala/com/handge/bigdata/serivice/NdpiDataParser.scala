/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.handge.bigdata.serivice

import com.handge.bigdata.config.Configure
import com.handge.bigdata.driver.BootStrap
import com.handge.bigdata.handers.{BytesParser, KafkaWriter, TagerProtocol, TagerUrlKeyword}
import com.handge.bigdata.handlerchain.HandlerChain
import com.handge.bigdata.pools.{EnvironmentContainer, Pools}


class NdpiDataParser extends BootStrap {

  val AppName = "ndpiparser"

  override def getAppName = {
    AppName
  }

  override def getConfiguration = NdpiDataParser.config


  override def getHandlerChain = NdpiDataParser.handlerChain
}


object NdpiDataParser {
  EnvironmentContainer.setENV()
  val config = Configure.getInstance(true).getDBConfiguration
  val pools = Pools.createPool(config)
  val handlerChain = new HandlerChain() {
    this.setHead(new BytesParser())
    this.addHandler(new TagerProtocol(pools.getJdbcConnectionPool))
    this.addHandler(new TagerUrlKeyword(pools.getJdbcConnectionPool))
    this.setTail(new KafkaWriter(pools.getKafkaConnectionPool))
  }


  def main(args: Array[String]): Unit = {
    new NdpiDataParser().runStreaming("local[3]")
  }
}
