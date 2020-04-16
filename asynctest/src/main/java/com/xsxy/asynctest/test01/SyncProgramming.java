package com.xsxy.asynctest.test01;

import java.util.concurrent.TimeUnit;

public class SyncProgramming {

    /**
     * 一般编程
     *
     * @param args
     */
    public static void main(String[] args){
        long start = System.currentTimeMillis();
        System.out.println("start:" + start);

        doSomething();

        doSomething1();

        // 大概：end:9009
        System.out.println("end:" + (System.currentTimeMillis() - start));

    }

    public static String doSomething(){
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "doSomething";
    }

    public static String doSomething1(){
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "doSomething1";
    }
}
