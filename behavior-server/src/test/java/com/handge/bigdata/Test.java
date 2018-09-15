package com.handge.bigdata;

//import com.handge.bigdata.resource.service.monitor.TopOfProtocolFlowImpl;

import com.handge.bigdata.resource.models.response.monitor.Illegal;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by DaLu Guo on 2018/4/27.
 */
public class Test {
    public static void main(String[] args) {
        Illegal illegal = new Illegal();
        illegal.setAccessTime(new Date());
        illegal.setAppName("qq");
        illegal.setIp("172.18.199.37");
        Illegal illegal1 = new Illegal();
        illegal1.setAccessTime(new Date());
        illegal1.setAppName("qq");
        illegal1.setIp("172.18.199.38");
        Illegal illegal2 = new Illegal();
        illegal2.setAccessTime(new Date());
        illegal2.setAppName("qq");
        illegal2.setIp("172.18.199.38");
        ArrayList<Illegal> arrayList = new ArrayList<>();
        System.out.println();
    }
}
