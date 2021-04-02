package com.roroldo.t6;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static com.roroldo.utils.Sleeper.sleep;

/**
 * 条件变量测试
 * @author 落霞不孤
 */
@Slf4j(topic = "c.ConditionObjectTest")
public class ConditionObjectTest {
    private final ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void testAwait() {
        lock.lock();
        try {
            log.info("begin wait...");
            condition.await();
            log.info("end wait...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void testSignal() {
        lock.lock();
        try {
            log.info("begin signal...");
            condition.signal();
            log.info("end signal...");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ConditionObjectTest conditionObjectTest = new ConditionObjectTest();

        ExecutorService service = Executors.newFixedThreadPool(2);

        service.submit(() -> conditionObjectTest.testAwait());
        sleep(1);
        service.submit(() -> conditionObjectTest.testSignal());
    }
}
