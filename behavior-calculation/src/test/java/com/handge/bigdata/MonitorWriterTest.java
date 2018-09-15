/*
 * Copyright (c) 2018  Inc. All rights reserved
 * Projects : net-behavior
 * Module : behavior-calculation
 * Class : MonitorWriterTest
 * User : XueFei Wang
 * Date : 7/4/18 10:24 AM
 * Modified :7/4/18 10:24 AM
 * Todo :
 *
 */

package com.handge.bigdata;

import com.handge.bigdata.config.Configure;
import com.handge.bigdata.handlerchain.BaseContext;
import com.handge.bigdata.handlerchain.Context;
import com.handge.bigdata.handlerchain.HandlerChain;
import com.handge.bigdata.handlers.MonitorWriter;
import com.handge.bigdata.pools.EnvironmentContainer;
import com.handge.bigdata.pools.Pools;
import com.handge.bigdata.proto.Behavior;
import com.handge.bigdata.proto.Behavior.BehaviorData;
import org.apache.commons.configuration2.Configuration;

import java.util.ArrayList;

public class MonitorWriterTest {

    public static void main(String[] args) throws Exception {

        EnvironmentContainer.setENV();

        Configuration config = Configure.getInstance(true).getDBConfiguration();

        Pools pools = Pools.createPool(config);

        MonitorWriter monitorWriter = new MonitorWriter(pools.getJdbcConnectionPool());

        HandlerChain handlerChain =  new HandlerChain();

        handlerChain.addHandler(monitorWriter);

        Context context = new BaseContext();

        context.setContext(makeData());

        handlerChain.execute(context);

    }

    public static ArrayList<BehaviorData> makeData(){
        ArrayList<BehaviorData> arrayList = new ArrayList<>();
        BehaviorData.Builder builder = BehaviorData.newBuilder();
        builder.setAppName("QQ飞车");
        builder.setIsnoise(false);
        builder.setAppProtocol("HTTP.QQ");
        builder.setDesIp("101.226.212.22");
        builder.setReceive(0);
        builder.setHost("isdspeed.qq.com");
        builder.setHttpUrl("");
        builder.setHttpMethod("");
        builder.setLocalIp("172.20.31.104");
        builder.setAppType("7.48/HTTP.QQ");
        builder.addAppTags("游戏");
        builder.setSend(2604);
        builder.setStartTime(1530201231002l);
        builder.setEndTime(1530501233735l);
        arrayList.add(builder.build());
        arrayList.add(builder.build());
        arrayList.add(builder.build());
        arrayList.add(builder.build());
        arrayList.add(builder.build());
        arrayList.add(builder.build());
        arrayList.add(builder.build());
        arrayList.add(builder.build());
        return  arrayList;
    }
}
