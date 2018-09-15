/*
 * Copyright (c) 2018  Inc. All rights reserved
 * Projects : net-behavior
 * Module : behavior-common
 * Class : CacheHeapTest
 * User : XueFei Wang
 * Date : 6/21/18 11:46 AM
 * Modified :6/21/18 11:46 AM
 * Todo :
 *
 */

package com.handge.bigdata;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.handge.bigdata.base.Tuple2;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CacheHeapTest {
    static final Cache<Integer, Tuple2<String, String>> tag_appname = CacheBuilder.newBuilder().maximumSize(80000).expireAfterWrite(3, TimeUnit.MINUTES).build();

    public static void main(String[] args) throws ExecutionException {
        int i = 0 ;
        while ( true){
            tag_appname.get(i, new Callable<Tuple2<String, String>>() {
                @Override
                public Tuple2<String, String> call() throws Exception {
                    return new Tuple2<>("a","b");
                }
            });
            i++;
        }
    }
}
