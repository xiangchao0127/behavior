/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.handge.bigdata.handlerchain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public interface Handler {

    public Log logger = LogFactory.getLog(Handler.class);

    boolean execute(Context context) throws Exception;

    default public void exception(String msg, Exception e) {
        logger.error(msg, e);
    }

}
