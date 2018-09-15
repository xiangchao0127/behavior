/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.handge.bigdata.handers;

import com.google.protobuf.InvalidProtocolBufferException;
import com.handge.bigdata.handlerchain.Context;
import com.handge.bigdata.handlerchain.Handler;
import com.handge.bigdata.proto.Behavior.BehaviorData;
import com.handge.bigdata.proto.Packet.PacketData;
import com.handge.bigdata.utils.IPDistinguishUtil;
import scala.collection.Iterator;

import java.util.ArrayList;

public class BytesParser implements Handler {
    @Override
    public boolean execute(Context context) throws Exception {
        try {
            ArrayList<BehaviorData> behaviorDataArrayList = new ArrayList<BehaviorData>();
            Iterator s = (Iterator) context.getContext();
            while (s.hasNext()) {
                byte[] r = (byte[]) s.next();
                PacketData p = PacketData.parseFrom(r);
                BehaviorData behaviorData = packet2Behavior(p);
                behaviorDataArrayList.add(behaviorData);
            }
            if (behaviorDataArrayList.isEmpty()) {
                return true;
            }
            context.setContext(behaviorDataArrayList);
        } catch (InvalidProtocolBufferException e) {
            exception(this.getClass().getName(), e);
        }
        return false;
    }

    /**
     * 转换ndpi数据
     *
     * @param packetData
     * @return
     */
    public BehaviorData packet2Behavior(PacketData packetData) {
        BehaviorData.Builder behaviorData = BehaviorData.newBuilder();
        //mac地址
        behaviorData.setDeviceId(packetData.getSrcMac());
        //协议
        String[] tem = packetData.getProto().replaceAll("]", "").trim().split("\\/");
        behaviorData.setAppProtocol(tem[tem.length - 1]);
        //访问时间
        behaviorData.setStartTime(packetData.getStartTime());
        //结束时间
        behaviorData.setEndTime(packetData.getEndTIme());

        behaviorData.setAppType(packetData.getProto());

        if (IPDistinguishUtil.internalIp(packetData.getSrcIp())) {
            //源IP
            behaviorData.setLocalIp(packetData.getSrcIp());
            //目的IP
            behaviorData.setDesIp(packetData.getDstIp());
            //上传流量
            behaviorData.setSend(packetData.getSrcBytes());
            //下载流量
            behaviorData.setReceive(0);

        } else {
            //源IP
            behaviorData.setLocalIp(packetData.getDstIp());
            //目的IP
            behaviorData.setDesIp(packetData.getSrcIp());
            //上传流量
            behaviorData.setSend(0);
            //下载流量
            behaviorData.setReceive(packetData.getSrcBytes());
        }
        //详情
        behaviorData.setDetails(packetData.getInfo());
        //域名
        behaviorData.setHost(packetData.getHost());
        //SSH/SSL客户端
        behaviorData.setClient(packetData.getClient());
        //SSH/SSL服务端
        behaviorData.setServer(packetData.getServer());

        behaviorData.setHttpUrl(packetData.getHttpURL());

        behaviorData.setHttpMethod(packetData.getHttpMethod());


        return behaviorData.build();
    }

}
