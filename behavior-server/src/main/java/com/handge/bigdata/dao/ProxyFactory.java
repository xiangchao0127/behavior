package com.handge.bigdata.dao;

import com.handge.bigdata.dao.proxy.Proxy;
import com.handge.bigdata.enumeration.DAOProxyEnum;

/**
 * Created by Liujuhao on 2018/4/28.
 */
public class ProxyFactory<T> {

    public static <T extends Proxy> T createProxy(DAOProxyEnum proxyEnum) {

        T proxy = null;
        try {
            proxy = (T) Class.forName(proxyEnum.getClazz().getName()).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return proxy;
    }

    @Deprecated
    public static <T> T createProxy(Class<T> t) {
        Object object = null;
        try {
            object = t.newInstance();

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return (T) object;
    }


}