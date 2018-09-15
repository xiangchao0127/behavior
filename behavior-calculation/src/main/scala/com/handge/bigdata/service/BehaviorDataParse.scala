/*
 * Copyright (c) 2018  Inc. All rights reserved
 * Projects : net-behavior
 * Module : behavior-calculation
 * Class : MiddleDataParse.scala
 * User : XueFei Wang
 * Date : 5/24/18 11:07 AM
 * Modified :5/23/18 1:12 PM
 * Todo :
 *
 */

package com.handge.bigdata.service

import com.handge.bigdata.config.Configure
import com.handge.bigdata.driver.BootStrap
import com.handge.bigdata.handlerchain.HandlerChain
import com.handge.bigdata.handlers.{BytesParser, ElasticWriter}
import com.handge.bigdata.pools.{EnvironmentContainer, Pools}

class BehaviorDataParse extends BootStrap {

  val appName = "behavior"

  override def getAppName = {
    appName
  }

  override def getConfiguration = BehaviorDataParse.config

  override def getHandlerChain = BehaviorDataParse.getChain
}

object BehaviorDataParse {

  EnvironmentContainer.setENV()
  val config = Configure.getInstance(true).getDBConfiguration
  val pools = Pools.createPool(config)
  val handlerChain = new HandlerChain() {
    this.setHead(new BytesParser)
    //    this.addHandler(new RedisWriter(pools.getJdbcConnectionPool, pools.getRedisPool))
    this.setTail(new ElasticWriter(pools.getEsConnectionPool))
  }


  def getChain: HandlerChain = {
    handlerChain
  }

  def main(args: Array[String]): Unit = {
    new BehaviorDataParse().runStreaming("local[3]")
  }
}