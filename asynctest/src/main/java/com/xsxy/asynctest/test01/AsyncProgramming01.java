package com.xsxy.asynctest.test01;

import java.util.concurrent.TimeUnit;

public class AsyncProgramming01 {

    /**
     * 简单创建thread
     *
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();
        System.out.println("start:" + start);

        Thread t = new Thread(AsyncProgramming01::doSomething);
        t.start();

        doSomething1();

        t.join();

        // 大概：end:5137
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
