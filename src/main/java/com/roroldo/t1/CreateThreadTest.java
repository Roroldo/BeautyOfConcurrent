package com.roroldo.t1;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * 创建线程的三种方式
 * @author 落霞不孤
 */
@Slf4j(topic = "c.CreateThreadTest")
public class CreateThreadTest {
    public static void main(String[] args) {
        // method1();
        // method2();
        method3();
    }


    /**
     * FutureTask 方式
     */
    public static void  method3() {
        FutureTask<String> task = new FutureTask<>(() -> "Hello ~");
        Thread t2 = new Thread(task, "t2");
        t2.start();

        // 获取线程 t2 执行后的返回结果
        try {
            String msg = task.get();
            log.info("get a msg is {}", msg);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * 实现 Runnable 接口
     */
    private static void method2() {
        Thread t1 = new Thread(() -> {
            log.info("t1 running....");
        }, "t1");

        t1.start();
    }

    /**
     * 继承 Thread 类并重写 run 方法
     */
    private static void method1() {
        MyThread myThread = new MyThread();
        myThread.start();
    }

    static class MyThread extends Thread {
        @Override
        public void run() {
            log.info("MyThread is running");
        }
    }
}
