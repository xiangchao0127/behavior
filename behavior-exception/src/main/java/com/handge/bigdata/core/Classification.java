//package com.handge.bigdata.core;
//
//import com.handge.bigdata.UnifiedException;
//import com.handge.bigdata.enumeration.ExceptionClassifyEnum;
//
///**
// * @auther Liujuhao
// * @date 2018/5/28.
// */
//public class Classification {
//
//    Wrapping wrapper = new Wrapping();
//    private ExceptionClassifyEnum plan = ExceptionClassifyEnum.PLAN;
//    private ExceptionClassifyEnum nonPlan = ExceptionClassifyEnum.NON_PLAN;
//
//    public UnifiedException classify(UnifiedException e) {
//        if (e.getClassifyEnum().equals(plan)) {
//            wrapper.warpPlan(e);
//        } else if (e.getClassifyEnum().equals(nonPlan)) {
//            wrapper.warpnNonPlan(e);
//        }
//        return e;
//    }
//
//}
