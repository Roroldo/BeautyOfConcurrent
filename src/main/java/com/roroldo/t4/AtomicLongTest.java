package com.roroldo.t4;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 原子累加类
 * @author 落霞不孤
 */
@Slf4j(topic = "c.AtomicLongTest")
public class AtomicLongTest {
    private static AtomicLong atomicLong = new AtomicLong();

    /**
     *  数据源
     */
    private static Integer[] arr1 = new Integer[]{0, 1, 3, -1, 0, 2, 10, 0};
    private static Integer[] arr2 = new Integer[]{12, 14, -2, -1, 0, 2, 6, 0};

    public static void main(String[] args) throws InterruptedException {
        // 开启两个线程，统计数据源 0 的个数
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < arr1.length; i++) {
                if (arr1[i] == 0) {
                    atomicLong.incrementAndGet();
                }
            }
        }, "t1");

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < arr2.length; i++) {
                if (arr2[i] == 0) {
                    atomicLong.incrementAndGet();
                }
            }
        }, "t2");

        t1.start();
        t2.start();
        // 等待t1、t2 线程结束
        t1.join();
        t2.join();
        log.debug("数据源 0 的个数是 {}", atomicLong.get());
    }
}
