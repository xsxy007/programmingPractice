package com.xsxy.asynctest.test04;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 广播调用RPC
 */
public class Test02RxJavaAsyncRpcCallTest {
    public static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    public static ThreadPoolExecutor BIZ_EXECUTOR = new ThreadPoolExecutor(AVAILABLE_PROCESSORS, AVAILABLE_PROCESSORS, 10, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(10), new ThreadPoolExecutor.CallerRunsPolicy());

    public static void main(String[] args) throws InterruptedException {
        // rxJavaRpcCall();
        // aSyncRxJavaRpcCall();
        // aSyncRpcCall2();
        aSync();
        // aSyncUserBizExecutor();
    }

    /**
     * rxjava 同步执行
     * 消耗时间大概为20s，因为rpcCall方法是同步调用的，调用线程就是main线程
     */
    public static void rxJavaRpcCall() {

        long start = System.currentTimeMillis();
        Flowable.fromArray(genIpList().toArray(new String[0]))
                .map(ip -> rpcCall(ip, ip))
                .subscribe(System.out::println);

        // 大概10s
        System.out.println("sync execute rxjavaRpcCall consume:" + (System.currentTimeMillis() - start));
    }

    /**
     * 异步调用
     * <p>
     * 在RxJava中，操作运算符不能直接使用Threads或ExecutorServices进行异步处理，而需要使用Schedulers来抽象统一API背后
     * 的并发调度线程池。RxJava提供了几个可通过Schedulers访问的标准调度执行器。
     * ● Schedulers.computation()：在后台运行固定数量的专用线程来计算密集型工作。大多数异步操作符使用它作为其默认调度线程池。
     * ● Schedulers.io()：在动态变化的线程集合上运行类I/ O或阻塞操作。
     * ● Schedulers.single()：以顺序和FIFO方式在单个线程上运行。
     * ● Schedulers.trampoline()：在其中一个参与线程中以顺序和FIFO方式运行，通常用于测试目的。
     * <p>
     * RxJava还可以让我们通过Schedulers.from（Executor）将现有的Executor（及其子类型，如ExecutorService）包装到Scheduler中。
     * 例如，可以将其用于具有更大但仍然固定的线程池（与calculate()和io()不同）
     */
    public static void aSyncRxJavaRpcCall() throws InterruptedException {

        long start = System.currentTimeMillis();
        // 使用 observeOn 让 rpcCall 的执行由main函数所在线程切换到IO线程
        // 顺序调用
        Flowable.fromArray(genIpList().toArray(new String[0]))
                // 切换到io线程执行
                .observeOn(Schedulers.io())
                // 映射结果
                .map(ip -> rpcCall(ip, ip))
                // 订阅消费者
                .subscribe(System.out::println);

        // main函数不会等rpcCall调用完毕
        System.out.println("sync execute rxjavaRpcCall consume:" + (System.currentTimeMillis() - start));

        // 上边代码在没有执行完10调用，main函数就结束了，因为IO线程时Deamon线程，而JVM退出的时机时没有用户线程
        // 所以需要将main函数挂起
        Thread.currentThread().join();

        // ##########################################################################################
        /*
        上代码我们挂起了main函数所在线程，上面的代码运行时main函数所在线程会马上返回，然后执行sout输出打印，
        并挂起自己；具体的10次rpc调用是在IO线程内执行的，到这里我们释放了main函数所在线程来执行rpc调用，但是IO线程
        内的10个rpc调用还是顺序执行的
         */
    }

    /**
     * 让10个rpc调用顺序执行转换为异步并发执行前，我们先看看另外一个操作符subscribeOn是如何在发射元素的线程执行比较耗时
     * 的操作时切换为异步执行的
     */
    public static void aSyncRpcCall2() throws InterruptedException {

        long start = System.currentTimeMillis();
        Flowable.fromCallable(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Done";
        })
                // 发射元素异步执行
                // .subscribeOn(Schedulers.io())
                // 切换到io线程执行
                .observeOn(Schedulers.io())
                // 订阅消费者
                .subscribe(System.out::println);

        // 该输出语句按理说时会很快输出的，但是事实并不是,还是会消耗2s左右，这个是因为，虽然消费时异步
        // 使用observeOn方法让接收元素和处理元素的逻辑从main函数所在线程切换为其他线程,但是发射元素还是同步执行的
        // 所以我们还需要让发射元素的逻辑异步化，而subscribeOn就是做这个事情的
        // (放开上边的subscribeOn方法，这样使用subscribeOn元素发射与observeOn接收操作全部都异步化)
        System.out.println("consume:" + (System.currentTimeMillis() - start));

        Thread.currentThread().join();


        // ##########################################################################################
        /*
        默认情况下被观察对象与其上施加的操作符链的运行以及把运行结果通知给观察者对象使用的是调用subscribe方法所在的
        线程，SubscribeOn操作符可以通过设置Scheduler来改变这个行为，让上面的操作切换到其他线程来执行。ObserveOn操
        作符可以指定一个不同的Scheduler让被观察者对象使用其他线程来把结果通知给观察者对象，并执行观察者的回调函数。

        所以如果流发射元素时有耗时的计算或者阻塞IO，则可以通过使用subscribeOn操作来把阻塞的操作异步化（切换到其他线
        程来执行）。另外如果一旦数据就绪（数据发射出来），则可以通过使用observeOn来切换使用其他线程（比如前台或者GUI
        线程）来对数据进行处理。

        需要注意SubscribeOn这个操作符指定的是被观察者对象(发布者)本身在哪个调度器上执行，而且和在流上的操作链中SubscribeOn的
        位置无关，并且整个调用链上调用多次时，只有第一次才有效。而ObservableOn则是指定观察者对象(订阅者)在哪个调度器上接收被观
        察者发来的通知，在操作符链上每当调用了ObservableOn这个操作符时都会进行线程的切换
         */
    }


    /**
     * 回到10次rpc调用，如何使用flatmap和subscribeOn将同步转为异步
     */
    public static void aSync() {
        long start = System.currentTimeMillis();
        Flowable.fromArray(genIpList().toArray(new String[0]))
                // flatMap 将所有的ip转换为 flowAble对象
                .flatMap(ip ->
                        // 将每个ip作为数据源获取一个流对象
                        Flowable.just(ip)
                                // 讲发射逻辑改为异步
                                .subscribeOn(Schedulers.io())
                                // 使用map将ip对象转为rpc调用结果  以上ipList所有的数据都是并发调用的
                                .map(v -> rpcCall(v, v)))
                // 阻塞所有的rpc并发调用结束 阻塞的是main线程
                .blockingSubscribe(System.out::println);

        // 因为rpc调用是并发进行的，所以耗时大概为2.5秒
        System.out.println("async consume: " + (System.currentTimeMillis() - start));
    }

    /**
     * 回到10次rpc调用，如何使用flatmap和subscribeOn将同步转为异步
     * <p>
     * 使用自定义线程池
     */
    public static void aSyncUserBizExecutor() {
        long start = System.currentTimeMillis();
        Flowable.fromArray(genIpList().toArray(new String[0]))
                // flatMap 将所有的ip转换为 flowAble对象
                .flatMap(ip ->
                        // 将每个ip作为数据源获取一个流对象
                        Flowable.just(ip)
                                // 讲发射逻辑改为异步
                                .subscribeOn(Schedulers.from(BIZ_EXECUTOR))
                                // 使用map将ip对象转为rpc调用结果  以上ipList所有的数据都是并发调用的
                                .map(v -> rpcCall(v, v)))
                // 阻塞所有的rpc并发调用结束 阻塞的是main线程
                .blockingSubscribe(System.out::println);

        // 因为rpc调用是并发进行的，所以耗时大概为6.6秒
        System.out.println("async consume: " + (System.currentTimeMillis() - start));
    }

    /**
     * 简单的rpcCall
     *
     * @param ip
     * @param params
     */
    public static String rpcCall(String ip, String params) {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return params;
    }

    /**
     * 生成ipList
     *
     * @return
     */
    public static List<String> genIpList() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("192.168.0." + i);
        }
        return list;
    }
}
