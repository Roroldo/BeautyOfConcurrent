package com.roroldo.t6;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedLongSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 测试 NonReentrantLock
 * @author 落霞不孤
 */
@Slf4j(topic = "c.NonReentrantLockTest")
public class NonReentrantLockTest {
    private final static NonReentrantLock LOCK = new NonReentrantLock();
    private final static Condition EMPTY = LOCK.newCondition();
    private final static Condition FULL = LOCK.newCondition();

    private final static Queue<String> QUEUE = new LinkedBlockingQueue<>();
    private final static int CAPACITY = 10;

    public static void main(String[] args) {
        Thread product = new Thread(() -> {
            // 获取独占锁
            LOCK.lock();
            try {
                // 如果队列满了，则等待
                while (QUEUE.size() == CAPACITY) {
                    log.info(Thread.currentThread().getName() + "await...");
                    FULL.await();
                }
                log.info(Thread.currentThread().getName() + " 生产 ele");
                // 添加元素到队列
                QUEUE.add("ele");
                // 唤醒消费者
                log.info(Thread.currentThread().getName() + " 唤醒所有的消费者");
                EMPTY.signalAll();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // 释放锁
                LOCK.unlock();
            }

        }, "product");

        Thread customer = new Thread(() -> {
            // 获取独占锁
            LOCK.lock();
            try {
                // 如果队列为空，则等待
                while (QUEUE.size() == 0) {
                    log.info(Thread.currentThread().getName() + " await...");
                    EMPTY.await();
                }
                // 移除元素
                String str = QUEUE.poll();
                log.info(Thread.currentThread().getName() + " 消费 " + str);
                // 唤醒生产者
                log.info(Thread.currentThread().getName() + " 唤醒所有的生产者");
                FULL.signalAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                LOCK.unlock();
            }
        }, "customer1");

        customer.start();
        product.start();
    }
}


/**
 * 基于 AQS 实现不可重入的独占锁
 */
class NonReentrantLock implements Lock, Serializable {

    // 辅助同步器
    static class MySync extends AbstractQueuedLongSynchronizer {
        /**
         * state == 1 表明当前线程获取锁成功
         * @param acquires acquires
         * @return true 获取锁成功
         */
        @Override
        protected boolean tryAcquire(long acquires) {
            assert acquires == 1;
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        /**
         * 释放锁，把 state 变为0
         * @param releases releases
         * @return true 代表当前线程释放锁成功
         */
        @Override
        protected boolean tryRelease(long releases) {
            assert releases == 1;
            if (getState() == 0) {
                throw new IllegalMonitorStateException();
            }
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        /**
         * state == 1 表示锁已经被占用了
         * @return true 锁已经被占用了
         */
        @Override
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        /**
         * 提供条件变量接口
         * @return 条件变量
         */
        Condition newCondition() {
            return new ConditionObject();
        }
    }


    /**
     * 创建一个 MySync 完成具体的工作
     */
    private final MySync sync = new MySync();

    /**
     * 加锁，失败则进入等待队列
     */
    @Override
    public void lock() {
        sync.acquire(1);
    }

    /**
     * 可打断的加锁
     * @throws InterruptedException e
     */
    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    /**
     * 尝试加锁
     * @return true 加锁成功
     */
    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    /**
     * 在指定的时间内尝试加锁
     * @param time time
     * @param unit unit
     * @return true 加锁成功
     * @throws InterruptedException e
     */
    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }

    /**
     * 解锁
     */
    @Override
    public void unlock() {
        sync.release(1);
    }

    /**
     * 锁是否被占用
     * @return true 锁被占用
     */
    public boolean isLocked() {
        return sync.isHeldExclusively();
    }

    /**
     * 返回一个条件变量
     * @return 条件变量
     */
    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }
}
