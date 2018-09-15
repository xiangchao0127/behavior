/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.handge.bigdata.handlerchain;

import java.util.HashMap;
import java.util.Map;

public class BaseContext<T> implements Context<T> {

    private T t = null;

    private Map<String, Object> meta = new HashMap();

    @Override
    public T getContext() {
        return this.t;
    }

    @Override
    public void setContext(T t) {
        this.t = t;

    }

    @Override
    public Map getMeta() {
        return this.meta;
    }

    @Override
    public void setMeta(Map meta) {
        this.meta = meta;

    }

    @Override
    public void putMeta(String key, Object value) {
        meta.put(key, value);
    }

    @Override
    public Object getMetaValue(String key) {
        return meta.get(key);
    }

    @Override
    public void destroy() {
        t = null;
        meta = null;
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer("Context = {  ");
        stringBuffer.append(t.toString()).append("    +    ").append(meta).append("}");
        return stringBuffer.toString();
    }
}
