/*
 * Copyright (c) 2018  Inc. All rights reserved
 * Projects : net-behavior
 * Module : behavior-common
 * Class : ThreeTuple
 * User : XueFei Wang
 * Date : 5/31/18 10:36 AM
 * Modified :5/30/18 2:42 PM
 * Todo :
 *
 */

package com.handge.bigdata.base;

public class Tuple3<A, B, C> extends Tuple2<A, B> {
    public final C c;

    public Tuple3(A a, B b, C c) {
        super(a, b);
        this.c = c;
    }

    public C getC() {
        return get_3();
    }

    public C get_3() {
        return c;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tuple3) {
            Tuple3<A, B, C> tmp = (Tuple3<A, B, C>) obj;
            if (tmp.a.equals(a) && tmp.b.equals(b) && tmp.c.equals(c)) {
                return true;
            }
        }
        return false;
    }
}