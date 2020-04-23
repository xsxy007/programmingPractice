package com.xsxy.asynctest.test02;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class AsyncFutureTaskExample {


    public static String doSomething() {
        try {
            TimeUnit.SECONDS.sleep(5);
            System.out.println("dosomething");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "doSomething";
    }

    public static String doSomething1() {
        try {
            TimeUnit.SECONDS.sleep(4);
            System.out.println("dosomething1");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "doSomething1";
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();

        FutureTask<String> futureTask = new FutureTask<String>((AsyncFutureTaskExample::doSomething));
        new Thread(futureTask, "threadA").start();

        String s1 = doSomething1();

        String s = futureTask.get();
        System.out.println(s + ":" + s1);
        // time:5309
        System.out.println("time:" + (System.currentTimeMillis() - start));

    }
}
