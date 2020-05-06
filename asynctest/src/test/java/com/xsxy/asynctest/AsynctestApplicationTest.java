package com.xsxy.asynctest;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.Assert.*;

public class AsynctestApplicationTest {


    @Test
    public void test(){

        StepVerifier.create(
                Flux.just("flux", "mono")
                        .flatMap(s -> Flux.fromArray(s.split("\\s*"))   // 1.对于每一个字符串s，将其拆分为包含一个字符串流
                                .delayElements(Duration.ofMillis(100))) // 2.对于每个元素延迟100ms
                        .doOnNext(System.out::print)) // 3.对每个元素进行打印
                .expectNextCount(8) // 4.验证是否发出8个元素
                .verifyComplete();
    }

}