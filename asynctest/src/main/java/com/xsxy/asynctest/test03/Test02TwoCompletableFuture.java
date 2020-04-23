package com.xsxy.asynctest.test03;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;


public class Test02TwoCompletableFuture {


    /**
     * CompletableFuture功能强大的原因之一是其可以让两个或者多个Completable-Future进行运算来产生结果
     * <p>
     * main函数中首先调用方法doSomethingOne("123")开启了一个异步任务，并返回了对应的CompletableFuture对象，
     * 我们取名为future1，然后在future1的基础上调用了thenCompose方法，企图让future1执行完毕后，激活使用其结果作
     * 为doSomethingTwo(String companyId)方法的参数的任务
     */
    public static void main(String[] args) throws Exception {
        // 串行
        // testThenCompose();
        // 并行计算
        // testThencombine();
        // 批量
        testAllOf();

    }

    /**
     * 基于thenCompose实现当一个CompletableFuture执行完毕后，执行另外一个CompletableFuture
     */
    public static void testThenCompose() throws Exception {
        CompletableFuture<String> future = doSomething1("123").thenCompose(id -> doSomething2(id));
        // CompletableFuture<String> future = doSomething1("123").thenCompose(Test02TwoCompletableFuture::doSomething2);
        System.out.println("main");
        System.out.println(future.get());
        System.out.println("main end");
    }

    /**
     * 基于thenCombine实现当一个CompletableFuture执行完毕后，执行另外一个CompletableFuture
     */
    public static void testThencombine() throws Exception {
        // CompletableFuture<String> future = doSomething1("123").thenCombine(doSomething2("456"), (str1, str2) -> str1 + ":" + str2);
        CompletableFuture<String> future = doSomething1("123").thenCombine(doSomething2("456"), (str1, str2) -> {
            return str1 + ":" + str2;
        });
        System.out.println("main");
        System.out.println(future.get());
        System.out.println("main end");
    }

    /**
     * 基于allOf等待多个并发运行的CompletableFuture任务执行完毕
     *
     * 调用了四次doSomethingOne方法，分别返回一个CompletableFuture对象，然后收集这些CompletableFuture到futureList列表。
     * 调用allOf方法把多个CompletableFuture转换为一个result，代码3在result上调用get()方法会阻塞调用线程，
     * 直到futureList列表中所有任务执行完毕才返回
     */
    public static void testAllOf() throws ExecutionException, InterruptedException {
        List<CompletableFuture<String>> futureList = new ArrayList<>();
        futureList.add(doSomething1("1"));
        futureList.add(doSomething1("2"));
        futureList.add(doSomething1("3"));
        futureList.add(doSomething1("4"));

        CompletableFuture<Void> res = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()]));
        System.out.println("main");
        // 等待所有的future执行完毕
        System.out.println(res.get());
        System.out.println("main end ");

    }


    public static CompletableFuture<String> doSomething1(String id) {
        return CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                System.out.println("doSomething1 execute");
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (Exception e) {

                }
                return id;
            }
        });
    }

    public static CompletableFuture<String> doSomething2(String id) {
        return CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                System.out.println("doSomething2 execute");
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (Exception e) {

                }
                return id + "doSomething2";
            }
        });
    }
}
