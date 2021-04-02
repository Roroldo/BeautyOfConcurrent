package com.roroldo.t6;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.locks.LockSupport;

import static com.roroldo.utils.Sleeper.sleep;

/**
 * 测试 park 和 unpark 方法
 * @author 落霞不孤
 */
@Slf4j(topic = "c.ParkAndUnparkTest")
public class ParkAndUnparkTest {

    @Test
    public void test1() {
        log.info("begin park....");
        // 调用此方法时 线程被挂起，不会结束
        LockSupport.park();
        log.info("end park....");
    }

    @Test
    public void test2() {
        log.info("begin park....");
        // 拿到许可证就不会再停止了
        LockSupport.unpark(Thread.currentThread());
        LockSupport.park();
        log.info("end park....");
    }

    @Test
    public void test3() {
        log.info("begin park....");
        LockSupport.park();
        // 这里注意啊！！！ 调用 park 的线程已经被阻塞了，它是永远不可能调用 unpark 方法的
        // 除非有其他线程 unpark 该线程
        LockSupport.unpark(Thread.currentThread());
        log.info("end park....");
    }

    @Test
    public void test4() {
        Thread t = new Thread(() -> {
            log.info("child thread begin park...");
            LockSupport.park();
            log.info("child thread end unpark!");
        }, "t");

        t.start();
        sleep(1);

        log.info("main thread begin unpark...");
        // 主线程 unpark t，t 继续运行
        LockSupport.unpark(t);
    }


    /**
     * 调用线程类的interrupted方法，其本质只是设置该线程的中断标志，将中断标志设置为true，
     * 并根据线程状态决定是否抛出异常。
     * 因此，通过interrupted方法真正实现线程的中断原理是：
     *  开发人员根据中断标志的具体值，来决定如何退出线程。
     */
    @Test
    public void test5() {
        Thread t = new Thread(() -> {
            log.info("child thread begin park...");
            // 只要被打断就退出循环
            while (!Thread.currentThread().isInterrupted()) {
                LockSupport.park();
            }
            log.info("child thread end unpark!");
        }, "t");

        t.start();
        sleep(1);

        log.info("main thread begin interrupt...");
        t.interrupt();
    }

    static class TestPark {
        public void testPark(ParkAndUnparkTest parkAndUnparkTest) {
            LockSupport.park();
        }
    }

    @Test
    public void test6() {
        TestPark testPark = new TestPark();
        testPark.testPark(this);
    }
}
