/*
 * Copyright (c) 2018  Inc. All rights reserved
 * Projects : net-behavior
 * Module : behavior-common
 * Class : IPDistinguishUtilTest
 * User : XueFei Wang
 * Date : 5/24/18 3:01 PM
 * Modified :5/24/18 3:01 PM
 * Todo :
 *
 */

package com.handge.bigdata;

import com.handge.bigdata.utils.IPDistinguishUtil;

public class IPDistinguishUtilTest {

    public static void main(String[] args) {
        System.out.println(IPDistinguishUtil.internalIp("172.18.199.34"));
        System.out.println(IPDistinguishUtil.internalIp("172.16.199.34"));
        System.out.println(IPDistinguishUtil.internalIp("172.10.199.34"));
        System.out.println(IPDistinguishUtil.internalIp("172.50.199.34"));
        System.out.println(IPDistinguishUtil.internalIp("10.18.199.34"));
        System.out.println(IPDistinguishUtil.internalIp("192.18.199.34"));
        System.out.println(IPDistinguishUtil.internalIp("17.18.199.34"));
        System.out.println(IPDistinguishUtil.internalIp("12.18.199.34"));
    }
}
