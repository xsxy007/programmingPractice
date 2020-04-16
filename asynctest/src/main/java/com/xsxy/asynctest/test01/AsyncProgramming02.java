package com.xsxy.asynctest.test01;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AsyncProgramming02 {

    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    private static final ThreadPoolExecutor EXECUTORS = new ThreadPoolExecutor(AVAILABLE_PROCESSORS, AVAILABLE_PROCESSORS,
            30, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10), new ThreadPoolExecutor.CallerRunsPolicy());


    /**
     * 利用线程池
     * @param args
     */
    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        // 这两个是不需要获取返回值得
        // EXECUTORS.execute(AsyncProgramming02::doSomething);
        // EXECUTORS.execute(AsyncProgramming02::doSomething1);
        // 需要返回值
        Future<String> something = EXECUTORS.submit(AsyncProgramming02::doSomething);
        Future<String> something1 = EXECUTORS.submit(AsyncProgramming02::doSomething1);


        // end:101   主线程101毫秒就执行完了，线程池中的线程会稍后执行
        System.out.println("end:" + (System.currentTimeMillis() - start));
        System.out.println(something.get() + "---" + something1.get());

        // end:5119
        System.out.println("time: " + (System.currentTimeMillis() - start));

        Thread.currentThread().join();

        // 如上代码确实可以在main函数所在线程获取到异步任务的执行结果，但是main线程必须以阻塞的代价来获取结果，
        // 在异步任务执行完毕前，main函数所在线程就不能做其他事情了，这显然不是我们所需要的
    }


    public static String doSomething(){
        try {
            TimeUnit.SECONDS.sleep(5);
            System.out.println("dosomething");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "doSomething";
    }

    public static String doSomething1(){
        try {
            TimeUnit.SECONDS.sleep(4);
            System.out.println("dosomething1");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "doSomething1";
    }
}
