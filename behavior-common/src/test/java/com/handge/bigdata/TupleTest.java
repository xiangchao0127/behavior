/*
 * Copyright (c) 2018  Inc. All rights reserved
 * Projects : net-behavior
 * Module : behavior-common
 * Class : TupleTest
 * User : XueFei Wang
 * Date : 6/1/18 11:16 AM
 * Modified :6/1/18 11:16 AM
 * Todo :
 *
 */

package com.handge.bigdata;

import com.handge.bigdata.base.Tuple2;

public class TupleTest {

    public static void main(String[] args) {
        Tuple2<String, String> t1 = new Tuple2<>("A", "b");
        Tuple2<String, String> t2 = new Tuple2<>("A", "b");

        System.out.println(t1 == t2);
        System.out.println(t1.equals(t2));
    }
}
