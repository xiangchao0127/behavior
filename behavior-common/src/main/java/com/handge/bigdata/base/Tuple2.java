/*
 * Copyright (c) 2018  Inc. All rights reserved
 * Projects : net-behavior
 * Module : behavior-common
 * Class : TwoTuple
 * User : XueFei Wang
 * Date : 5/31/18 10:36 AM
 * Modified :5/30/18 2:42 PM
 * Todo :
 *
 */

package com.handge.bigdata.base;

import java.io.Serializable;

public class Tuple2<A, B> implements Serializable {
    public final A a;
    public final B b;

    public Tuple2(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A getA() {
        return get_1();
    }

    public B getB() {
        return get_2();
    }

    public A get_1() {
        return a;
    }

    ;

    public B get_2() {
        return b;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tuple2) {
            Tuple2<A, B> tmp = (Tuple2<A, B>) obj;
            if (tmp.a.equals(a) && tmp.b.equals(b)) {
                return true;
            }
        }
        return false;
    }

}
