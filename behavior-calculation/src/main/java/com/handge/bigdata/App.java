package com.handge.bigdata;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        int a = 2;

        if (a == 2){
            System.out.println("AAAAAAA");
        }else if (a >1){
            System.out.println("BBBBBBBB");
        }
        else if (2 == 2){
            System.out.println("CCCCCCC");
        }

        try {
            test();
        } catch (Exception e) {
            System.out.println("============================");
        }
    }


    public static void test() {
        try {
            throw new RuntimeException();
        } catch (Exception e) {
            throw new RuntimeException("ddddddddddddddd");
        } finally {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
    }
}
