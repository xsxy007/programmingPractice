package com.xsxy.asynctest.test05;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 要使用reactor需要引入pom文件
 * https://projectreactor.io/docs/core/release/reference/
 */
public class Test01ReactorPractice {

    public static void main(String[] args) throws InterruptedException {
        // generateFlux1To6();
        // generateMoneWithError();
        // flatMapTest();
        // syncToAsync();
        // onErrorHandler();
        // onErrorHandler1();
        // testBackpressure();
        zipOperators();
    }


    public static void generateFlux1To6() {
        Flux.just(1, 2, 3, 4, 5, 6).subscribe(System.out::println);
    }

    public static void generateMoneWithError() {
        Mono.error(new Exception("some error")).subscribe(System.out::println, System.err::println);
    }

    public static void flatMapTest() {

        Flux.just("flux", "mono")
                .flatMap(s -> Flux.fromArray(s.split("\\s*"))
                        .delayElements(Duration.ofMillis(100)))
                .doOnNext(System.out::println);
    }

    /**
     * 同步转异步
     */
    public static void syncToAsync() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        long start = System.currentTimeMillis();
        Mono.fromCallable(() -> getStringSync())
                .subscribeOn(Schedulers.elastic())
                .subscribe(System.out::println, null, countDownLatch::countDown);
        System.out.println("end :" + (System.currentTimeMillis() - start));
        // 这个countdownlatch 是为了等待getStringSync()方法结束
        countDownLatch.await(10, TimeUnit.SECONDS);
        System.out.println("main end:" + (System.currentTimeMillis() - start));
    }

    private static String getStringSync() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Hello, Reactor!";
    }

    /**
     * 错误处理
     * 返回静态缺省值
     */
    public static void onErrorHandler() {
        Flux.range(1, 6)
                .map(i -> 10 / (i - 3))
                .onErrorReturn(0)
                .map(i -> i * i)
                .subscribe(System.out::println, System.err::println);
    }

    /**
     * 错误处理
     * 捕获并执行一个异常处理方法或计算一个候补值来顶替
     */
    public static void onErrorHandler1() {
        Flux.range(1, 6)
                .map(i -> 10 / (i - 3))
                .onErrorResume(e -> Mono.just(new Random().nextInt(6))) // 提供新的数据流
                .map(i -> i * i)
                .subscribe(System.out::println, System.err::println);
    }

    /**
     * 测试回压
     * 1. Flux.range是一个快的Publisher；
     * 2. 在每次request的时候打印request个数；
     * 3. 通过重写BaseSubscriber的方法来自定义Subscriber；
     * 4. hookOnSubscribe定义在订阅的时候执行的操作；
     * 5. 订阅时首先向上游请求1个元素；
     * 6. hookOnNext定义每次在收到一个元素的时候的操作；
     * 7. sleep 1秒钟来模拟慢的Subscriber；
     * 8. 打印收到的元素；
     * 9. 每次处理完1个元素后再请求1个。
     */
    public static void testBackpressure() {
        // 1.是一个快的Publiser
        Flux.range(1, 6)
                // 2.每次request的时候打印request的个数
                .doOnRequest(n -> System.out.println("Request " + n + " values..."))
                // 3.通过重写BaseSubscriber的方法来自定义Subscriber
                .subscribe(new BaseSubscriber<Integer>() {
                    // 4.在订阅的时候执行
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        System.out.println("Subscribed and make a request...");
                        request(1); // 5.订阅时首先向上游请求1个元素
                    }

                    // 6.每次收到一个元素的时候操作
                    @Override
                    protected void hookOnNext(Integer value) {
                        try {
                            // 7.模拟慢的subscriber
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // 8
                        System.out.println("Get value [" + value + "]");
                        // 9
                        request(1);
                    }
                });
    }


    public static void zipOperators() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Flux.zip(genFlux(), Flux.interval(Duration.ofMillis(200)))
                // subscribe中的t就是值得zip合并后的tuple元组，t1就是genFlux()方法的返回（这么理解）
                .subscribe(t -> System.out.println(t.getT1()), null, countDownLatch::countDown);

        countDownLatch.await(10, TimeUnit.SECONDS);
    }

    private static Flux<String> genFlux() {
        String desc = "Zip two sources together, that is to say wait for all the sources to emit one element and combine these elements once into a Tuple2.";
        return Flux.fromArray(desc.split("\\s+"));
    }
}
