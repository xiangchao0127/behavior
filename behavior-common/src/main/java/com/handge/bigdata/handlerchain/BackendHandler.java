/*
 * Copyright (c) 2018  Inc. All rights reserved
 * Projects : net-behavior
 * Module : behavior-common
 * Class : BackendHandler
 * User : XueFei Wang
 * Date : 5/31/18 10:53 AM
 * Modified :5/31/18 10:21 AM
 * Todo :
 *
 */

package com.handge.bigdata.handlerchain;

import com.handge.bigdata.base.BaseHandler;
import com.handge.bigdata.pools.common.AbstractPool;


public abstract class BackendHandler<T, P extends AbstractPool<T>> extends BaseHandler<T, P> implements Handler {

    public BackendHandler(P pool) {
        super(pool);
    }


    @Override
    public boolean execute(Context context) throws Exception {
        Object result = write(context);
        if (result != null) {
            context.putMeta(this.getClass().getName(), result);
        }
        return false;
    }

    public Object write(Context context) {
        return new WrapTask<Object>() {
            @Override
            public Object call() throws Exception {
                return handle(this.clent, context);
            }
        }.run();
    }

    /**
     * @param client
     * @param context
     * @return
     */
    public abstract Object handle(T client, Context context);
}
