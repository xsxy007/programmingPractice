package com.xsxy.asynctest.test02;

import java.util.concurrent.*;

/**
 * FutureTask虽然提供了用来检查任务是否执行完成、等待任务执行结果、获取任务执行结果的方法，
 * 但是这些特色并不足以让我们写出简洁的并发代码，比如它并不能清楚地表达多个FutureTask之间的关系。
 * 另外，为了从Future获取结果，我们必须调用get()方法，而该方法还是会在任务执行完毕前阻塞调用线程，
 * 这明显不是我们想要的。我们真正想要的是：
 * ● 可以将两个或者多个异步计算结合在一起变成一个，这包含两个或者多个异步计算是相互独立的情况，也包含第二个异步计算依赖第一个异步计算结果的情况。
 * ● 对反应式编程的支持，也就是当任务计算完成后能进行通知，并且可以以计算结果作为一个行为动作的参数进行下一步计算，而不是仅仅提供调用线程以阻塞的方式获取计算结果。
 * ● 可以通过编程的方式手动设置（代码的方式）Future的结果；FutureTask不能实现让用户通过函数来设置其计算结果，而是在其任务内部来进行设置。
 * ● 可以等多个Future对应的计算结果都出来后做一些事情。为了克服FutureTask的局限性，以及满足我们对异步编程的需要，JDK8中提供了CompletableFuture
 */
public class AsyncFutureTaskExample01 {
    public static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    public static ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(AVAILABLE_PROCESSORS, AVAILABLE_PROCESSORS, 10, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(10), new ThreadPoolExecutor.CallerRunsPolicy());


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

        FutureTask<String> futureTask = new FutureTask<String>((AsyncFutureTaskExample01::doSomething));
        // 使用线程池
//        EXECUTOR.execute(futureTask);
        EXECUTOR.submit(futureTask);

        String s1 = doSomething1();

        String s = futureTask.get();
        System.out.println(s + ":" + s1);
        // time:5309
        System.out.println("time:" + (System.currentTimeMillis() - start));

        EXECUTOR.shutdown();
    }
}
