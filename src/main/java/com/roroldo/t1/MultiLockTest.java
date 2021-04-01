package com.roroldo.t1;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 多种共享锁
 * @author 落霞不孤
 */
@Slf4j(topic = "c.MultiLockTest")
public class MultiLockTest {
    private static volatile Object resourceA = new Object();
    private static volatile Object resourceB = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            synchronized (resourceA) {
                log.info("t1 get resourceA lock");
                synchronized (resourceB) {
                    log.info("t1 get resourceB lock");
                    try {
                        resourceA.wait();
                        log.info("t1 release resourceA lock");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "t1");

        Thread t2 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
                synchronized (resourceA) {
                    log.info("t2 get resourceA lock");
                    log.info("t2 try get resourceB lock");
                    synchronized (resourceB) {
                        log.info("t2 get resourceB lock");
                        try {
                            resourceA.wait();
                            log.info("t2 release resourceA lock");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t2");

        t1.start();
        t2.start();
        // 等待 t1,t2 运行结束
        t1.join();
        t2.join();
        log.info("main Thread end...");
    }
}
