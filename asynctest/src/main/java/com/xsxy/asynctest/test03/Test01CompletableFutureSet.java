package com.xsxy.asynctest.test03;

import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Test01CompletableFutureSet {
    public static final int AVALIABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    public static ThreadPoolExecutor executor = new ThreadPoolExecutor(AVALIABLE_PROCESSORS, AVALIABLE_PROCESSORS, 1, TimeUnit.MINUTES,
            new LinkedBlockingQueue<>(5), new ThreadPoolExecutor.CallerRunsPolicy());


    public static void main(String[] args) throws Exception {
        // waitNotify();
        // runAsync();
        // runAsync1WithBizExecutor();
        // supplyAsync();
        // thenRun();
        thenAccept();
    }

    /**
     * 这里使用CompletableFuture实现了 通知等待模型，主线程调用future的get()方法等待future返回结果，一开始由于future结果没有设置，
     * 所以主线程被阻塞挂起，等异步任务休眠3s，然后调用future的complete方法模拟主线程等待的条件完成，这时候主线程就会从get()方法返回。
     */
    public static void waitNotify() throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture completableFuture = new CompletableFuture();
        executor.execute(() -> {
            System.out.println("executor sleep 3 seconds");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (Exception e) {

            }

            completableFuture.complete("executor done");
        });

        System.out.println("main wait completableFurture result");
        // 阻塞获取completableFuture结果
        System.out.println(completableFuture.get());
        // 设置超时时间，超时时会保timeoutException异常
        // System.out.println(completableFuture.get(1, TimeUnit.SECONDS));
        System.out.println("main end");
    }

    /**
     * 实现无返回值的异步计算：当你想异步执行一个任务，并且不需要任务的执行结果时可以使用该方法，比如异步打日志，异步做消息通知等
     * 在默认情况下，runAsync(Runnablerunnable)方法是使用整个JVM内唯一的ForkJoinPool.commonPool()线程池来执行异步任务的，
     * 使用runAsync (Runnable runnable, Executor executor)
     */
    public static void runAsync() throws Exception {
        CompletableFuture future = CompletableFuture.runAsync(() -> {
            System.out.println("execute completableFuture task");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (Exception e) {

            }
        });

        System.out.println("mian");
        // 无返回值
        System.out.println(future.get());
        System.out.println("main end");
    }

    /**
     * 使用自定义线程池 实现无返回值的异步计算
     */
    public static void runAsync1WithBizExecutor() throws Exception {
        // 使用自定义的线程池
        CompletableFuture future = CompletableFuture.runAsync(() -> {
            System.out.println("run completableFuture task");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (Exception e) {

            }
        }, executor);

        System.out.println("main");
        System.out.println(future.get());
        System.out.println("main end");
    }

    /**
     * 实现有返回值的异步计算
     * 在默认情况下，supplyAsync(Supplier<U> supplier)方法是使用整个JVM内唯一的ForkJoinPool.commonPool()线程池来执行异步任务的，
     * 使用supply-Async(Supplier<U> supplier,Executor executor)方法允许我们使用自己制定的线程池来执行异步任务
     */
    public static void supplyAsync() throws Exception {
        CompletableFuture future = CompletableFuture.supplyAsync(new Supplier<Object>() {
            @Override
            public Object get() {
                System.out.println("completableFuture supplyAsync run");
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (Exception e) {

                }
                // 返回异步计算结果
                return "supplyAsync return";
            }
        });

        System.out.println("main");
        System.out.println(future.get());
        System.out.println("main end");
    }

    /**
     * 基于thenRun实现异步任务，执行完毕后，激活异步任务B执行，需要注意的是，这种方式激活的异步任务B是拿不到任务A的执行结果的
     */
    public static void thenRun() throws ExecutionException, InterruptedException {
        CompletableFuture future = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                System.out.println("completableFuture run");
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (Exception e) {

                }
                return "completableFuture supplyAsync return";
            }
        });

        CompletableFuture futuretwo = future.thenRun(() -> {
            System.out.println("thenRun  --");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {

            }
        });

        System.out.println("main");
        System.out.println(futuretwo.get());
        System.out.println("main end");
    }

    /**
     * 基于thenAccept实现异步任务，执行完毕后，激活异步任务B执行，需要注意的是，这种方式激活的异步任务B是可以拿到任务A的执行结果的
     */
    public static void thenAccept() throws ExecutionException, InterruptedException {
        CompletableFuture future = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                System.out.println("completableFuture run");
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (Exception e) {

                }
                return "completableFuture supplyAsync return";
            }
        });

        CompletableFuture futuretwo = future.thenAccept(new Consumer<String>() {
            @Override
            public void accept(String o) {
                System.out.println(o);
            }
        });

        System.out.println("main");
        System.out.println(futuretwo.get());
        System.out.println("main end");
    }
}
