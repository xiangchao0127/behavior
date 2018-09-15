/*
 * Copyright (c) 2018  Inc. All rights reserved
 * Projects : net-behavior
 * Module : behavior-common
 * Class : FourTuple
 * User : XueFei Wang
 * Date : 5/31/18 10:36 AM
 * Modified :5/31/18 10:21 AM
 * Todo :
 *
 */

package com.handge.bigdata.base;

public class Tuple4<A, B, C, D> extends Tuple3 {

    public final D d;

    public Tuple4(A a, B b, C c, D d) {
        super(a, b, c);
        this.d = d;
    }

    public D getD() {
        return get_4();
    }

    public D get_4() {
        return d;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tuple4) {
            Tuple4<A, B, C, D> tmp = (Tuple4<A, B, C, D>) obj;
            if (tmp.a.equals(a) && tmp.b.equals(b) && tmp.c.equals(c) && tmp.d.equals(d)) {
                return true;
            }
        }
        return false;
    }
}
