package com.xsxy.asynctest.test03;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Test03CompletableFutureWithException {
    /**
     * 之前的测试completableFuture都是基于流程正常流转，如果出现异常怎么处理
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
                // 测试异常
                if (true) {
                    throw new RuntimeException("");
                }
                future.complete("complete");
            } catch (Exception e) {
                e.printStackTrace();
                // 先注释掉， 和下边的输出语句同时放开（注释掉32，放开33）
                // future.completeExceptionally(e);
            }


        }, "thread-1").start();

        System.out.println("main");
        System.out.println(future.get());
        // System.out.println(future.exceptionally(t -> "aaaaaa").get());
        System.out.println("main end");
    }
}
