/*
 * Copyright (c) 2018  Inc. All rights reserved
 * Projects : net-behavior
 * Module : behavior-common
 * Class : FiveTuple
 * User : XueFei Wang
 * Date : 6/1/18 11:00 AM
 * Modified :6/1/18 11:00 AM
 * Todo :
 *
 */

package com.handge.bigdata.base;

public class Tuple5<A, B, C, D, E> extends Tuple4 {

    public final E e;

    public Tuple5(A a, B b, C c, D d, E e) {
        super(a, b, c, d);
        this.e = e;
    }

    public E getE() {
        return get_5();
    }

    public E get_5() {
        return e;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tuple4) {
            Tuple5<A, B, C, D, E> tmp = (Tuple5<A, B, C, D, E>) obj;
            if (tmp.a.equals(a) && tmp.b.equals(b) && tmp.c.equals(c) && tmp.d.equals(d) && tmp.e.equals(e)) {
                return true;
            }
        }
        return false;
    }
}
