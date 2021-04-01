package com.roroldo.pattern;

import lombok.extern.slf4j.Slf4j;

import static com.roroldo.utils.Sleeper.sleep;

/**
 * SingleThreadExecution 模式，意思是只能有一个线程执行临界区的代码。
 *
 *  情景：多个过桥的人（线程）安全地过独木桥
 * @author 落霞不孤
 */
@Slf4j(topic = "c.SingleThreadExecutionTest")
public class SingleThreadExecutionTest {
    public static void main(String[] args) {
        SinglePlankBridge bridge = new SinglePlankBridge();

        Thread t1 = new Thread(() -> {
            bridge.pass();
        }, "t1");

        Thread t2 = new Thread(() -> {
            bridge.pass();
        }, "t2");

        Thread t3 = new Thread(() -> {
            bridge.pass();
        }, "t3");

        t1.start();
        t2.start();
        t3.start();
    }
}


/**
 * 独木桥，模拟共享资源, 只能有一个人在桥上
 */
@Slf4j(topic = "c.SinglePlankBridge")
class SinglePlankBridge {

    public synchronized void pass(){
        log.info("{} 正在过桥", Thread.currentThread().getName());
        sleep(1);
        log.info("{} 已经过桥", Thread.currentThread().getName());
    }
}