package com.roroldo.t1;

import lombok.extern.slf4j.Slf4j;

import static com.roroldo.utils.Sleeper.sleep;

/**
 * 测试 notifyAll
 * @author 落霞不孤
 */
@Slf4j(topic = "c.NotifyAndNotifyAllTest")
public class NotifyAndNotifyAllTest {
    private static volatile Object lockA = new Object();
    private static volatile Object lockB = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            synchronized (lockA) {
                log.info("t1 get lockA");
                try {
                    log.info("t1 begin wait");
                    lockA.wait();
                    log.info("t1 end wait");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "t1");

        Thread t2 = new Thread(() -> {
            synchronized (lockA) {
                log.info("t2 get lockA");
                try {
                    log.info("t2 begin wait");
                    lockA.wait();
                    log.info("t2 end wait");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "t1");

        Thread t3 = new Thread(() -> {
            synchronized (lockA) {
                log.info("t3 get lockA");
                // log.info("t3 begin notify");
                // lockA.notify();
                log.info("t3 begin notifyAll");
                lockA.notifyAll();
            }
        }, "t1");


        t1.start();
        t2.start();
        sleep(1);
        t3.start();

        t1.join();
        t2.join();
        t3.join();
        log.info("main is over");
    }
}
