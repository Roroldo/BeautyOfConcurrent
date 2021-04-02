package com.roroldo.t6;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

import static com.roroldo.utils.Sleeper.sleep;

/**
 * 先进先出锁
 * @author 落霞不孤
 */
@Slf4j(topic = "c.FIFOMutex")
public class FIFOMutex {
    private final AtomicBoolean locked = new AtomicBoolean(true);  // locked 默认值 true
    private final Queue<Thread> waiters = new ConcurrentLinkedDeque<>();

    public void lock() {
        boolean wasInterrupted = false;
        Thread current = Thread.currentThread();
        waiters.add(current);

        // 只有队首的线程可以获取锁
        while (waiters.peek() != current || !locked.compareAndSet(false, true)) {
            LockSupport.park(this);
            // park 的过程中被其他线程中断，做个标记
            if (Thread.interrupted()) {
                wasInterrupted = true;
            }
        }

        waiters.remove();
        if (wasInterrupted) {
            current.interrupt();
        }
    }

    public void unLock() {
        locked.set(false);
        LockSupport.unpark(waiters.peek());
    }

    public void showWaiters() {
        List<Thread> list = new LinkedList<>(waiters);
        log.info(list.toString());
    }

    public static void main(String[] args) {
        FIFOMutex fifoMutex = new FIFOMutex();

        Thread firstThread = new Thread(() -> {
            log.info("firstThread start...");
            fifoMutex.lock();
            log.info("firstThread end...");
        }, "firstThread");
        firstThread.start();
        sleep(1);

        for (int i = 2; i <= 4; i++) {
            new Thread(() -> {
                fifoMutex.lock();
            }, "t" + i).start();
        }

        sleep(1);
        fifoMutex.showWaiters();

        // 恢复第一个线程的运行
        sleep(1);
        // fifoMutex.unLock();

        // interrupt 方法可以打断 firstThread 线程的 park，如果此时已经解锁则继续执行
        firstThread.interrupt();
    }
}
