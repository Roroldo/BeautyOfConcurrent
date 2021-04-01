package com.roroldo.pattern;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;

import static com.roroldo.utils.Sleeper.sleep;

/**
 * 每个消息每一个线程：
 *      意思就是你来一个请求我就开一个线程来响应，我接着干自己的活。
 * @author 落霞不孤
 */
@Slf4j(topic = "c.ThreadPerMessageTest")
public class ThreadPerMessageTest {
    public static void main(String[] args) {
        log.info("main begin...");
        Host host = new Host();
        host.request(2, 'A');
        host.request(4, 'B');
        host.request(6, 'C');
        log.info("main end...");
    }
}


/**
 * 主机
 */
@Slf4j(topic = "c.Host")
class Host {
    /**
     * 响应请求
     */
    private final Helper helper = new Helper();
    /**
     * 进程计数
     */
    private final AtomicLong counter = new AtomicLong();

    public void request(int count, final char c) {
        log.info("  request({}, {}) Begin", count, c);
        new Thread(() -> {
            helper.handler(count, c);
        }, "t" + counter.incrementAndGet()).start();
        log.info("  request({}, {}) End", count, c);
    }
}

/**
 * 响应者
 */
@Slf4j(topic = "c.Helper")
class Helper {

    public void handler(int count, char c) {
        log.info("      handle({}, {}) Begin", count, c);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
           sleep(1);
           sb.append(c);
        }
        log.info(sb.toString());
        log.info("      handle({}, {}) End", count, c);
    }
}
