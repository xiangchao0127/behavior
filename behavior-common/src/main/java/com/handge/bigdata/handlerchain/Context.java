/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.handge.bigdata.handlerchain;

import java.util.Map;

public interface Context<T> {

    public T getContext();

    public void setContext(T t);

    public Map getMeta();

    public void setMeta(Map map);

    public void putMeta(String key, Object value);

    public Object getMetaValue(String key);

    public void destroy();
}
