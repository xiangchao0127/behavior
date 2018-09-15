/*
 * Copyright (c) 2018  Inc. All rights reserved
 * Projects : net-behavior
 * Module : behavior-calculation
 * Class : BytesParser
 * User : XueFei Wang
 * Date : 5/29/18 7:25 PM
 * Modified :5/29/18 11:13 AM
 * Todo :
 *
 */

package com.handge.bigdata.handlers;

import com.handge.bigdata.handlerchain.Context;
import com.handge.bigdata.handlerchain.Handler;
import com.handge.bigdata.proto.Behavior.BehaviorData;
import scala.collection.Iterator;

import java.util.ArrayList;

public class BytesParser implements Handler {
    @Override
    public boolean execute(Context context) {
        try {
            ArrayList<BehaviorData> behaviorDatas = new ArrayList<BehaviorData>();
            Iterator s = (Iterator) context.getContext();
            while (s.hasNext()) {
                byte[] r = (byte[]) s.next();

                BehaviorData p = BehaviorData.parseFrom(r);
                behaviorDatas.add(p);
            }
            if (behaviorDatas.isEmpty()) {
                return true;
            }
            context.setContext(behaviorDatas);
        } catch (Exception e) {
            exception(this.getClass().getName(), e);
        }

        return false;
    }

}
