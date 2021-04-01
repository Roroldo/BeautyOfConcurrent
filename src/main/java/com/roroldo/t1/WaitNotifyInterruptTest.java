package com.roroldo.t1;

import lombok.extern.slf4j.Slf4j;

import static com.roroldo.utils.Sleeper.sleep;


/**
 * 正在 wait 的线程被中断
 * @author 落霞不孤
 */
@Slf4j(topic = "c.WaitNotifyInterruptTest")
public class WaitNotifyInterruptTest {
    private static Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            log.info("t1 begin...");
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "t1");

        t1.start();

        sleep(1);
        log.info("Main begin interrupt t1");
        t1.interrupt();
        log.info("Main end interrupt t1");
    }
}
