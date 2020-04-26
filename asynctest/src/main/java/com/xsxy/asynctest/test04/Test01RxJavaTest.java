package com.xsxy.asynctest.test04;

import io.reactivex.Flowable;

import java.util.ArrayList;
import java.util.List;

/**
 * Rxjava 练习
 * <p>
 * RxJava是Reactive Extensions的Java VM实现：RxJava是一个库，用于通过使用可观察序列来编写异步和基于事件的程序。
 * 它扩展了观察者模式以支持数据/事件序列，并添加了允许以声明方式组合数据序列的运算符，同时抽象出对低级线程、同步、线
 * 程安全和并发数据结构等问题的关注，RxJava试图做得非常轻量级，它仅仅作为单个JAR实现，仅关注Observable抽象和相关的
 * 高阶运算函数
 */
public class Test01RxJavaTest {

    public static void main(String[] args) {
        java8Pring();
        rxjavaPrint();
    }

    /**
     * 以java8stream的方式输出大于5岁的person的name
     */
    public static void java8Pring() {
        System.out.println("java8stream");
        List<Person> personList = makeList();
        personList.stream().filter(person -> person.age > 5)
                .map(Person::getName)
                .forEach(System.out::println);
    }

    /**
     * 以rxjava的方式输出大于5岁的person的name
     */
    public static void rxjavaPrint() {
        System.out.println("rxJava");
        List<Person> personList = makeList();
        // 转换列表
        Flowable.fromArray(personList.toArray(new Person[0]))
                // 过滤
                .filter(person -> person.age > 5)
                // 映射转换
                .map(Person::getName)
                // 订阅输出
                .subscribe(System.out::println);

    }

    public static List<Person> makeList() {
        List<Person> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(new Person("name" + i, i));
        }
        return list;
    }


    static class Person {
        private String name;
        private Integer age;

        public Person() {
        }

        public Person(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }
}
