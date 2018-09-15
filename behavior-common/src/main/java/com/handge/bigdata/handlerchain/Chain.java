/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.handge.bigdata.handlerchain;

import java.util.List;

public interface Chain extends Handler {

    void addHandler(Handler command);

    Handler getHead();

    void setHead(Handler head);

    Handler getTail();

    void setTail(Handler tail);

    List<Handler> getHandlers();

    /**
     * extcute handlers  in pipeline chain
     *
     * @param context
     * @return if return true will break chain  else next hander exetute {@link HandlerChain}
     * @throws Exception
     */
    boolean execute(Context context) throws Exception;

}
