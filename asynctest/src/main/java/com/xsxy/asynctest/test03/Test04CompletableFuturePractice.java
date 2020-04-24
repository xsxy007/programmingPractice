package com.xsxy.asynctest.test03;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 将一个简单远程调用的方式例子改为异步调用
 * <p>
 * 注意：具体这10个rpc请求是否全部并发运行取决于CompletableFuture内线程池内线程的个数，
 * 如果你的机器是单核的或者线程池内线程个数为1，那么这10个任务还是会顺序执行的
 */
public class Test04CompletableFuturePractice {

    public static void main(String[] args) {
        List<String> ipList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ipList.add("192.168.0." + i);
        }
        syncMethod(ipList);
        AsyncMethod(ipList);
    }

    /**
     * 同步顺序调用耗时 time:10138
     *
     * @param ipList
     */
    public static void syncMethod(List<String> ipList) {
        long start = System.currentTimeMillis();
        ipList.forEach(ip -> {
            rpcCall(ip, 8080);
        });
        System.out.println("time:" + (System.currentTimeMillis() - start));
    }

    /**
     * 异步调用耗时 time:4029
     *
     * @param ipList
     */
    public static void AsyncMethod(List<String> ipList) {
        long start = System.currentTimeMillis();
        // 同步调用转异步
        List<CompletableFuture<String>> completableFutureList = ipList.stream().map(ip -> CompletableFuture.supplyAsync(() -> rpcCall(ip, 9090)))
                .collect(Collectors.toList());

        // 阻塞等待所有调用都结束
        List<String> resList = completableFutureList.stream().map(CompletableFuture::join).collect(Collectors.toList());
        // resList.forEach(System.out::println);

        System.out.println("time:" + (System.currentTimeMillis() - start));
    }


    public static String rpcCall(String ip, int port) {
        System.out.println("rpcCall=ip:" + ip + ",port:" + port);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (Exception e) {

        }
        return "res" + port;
    }
}
