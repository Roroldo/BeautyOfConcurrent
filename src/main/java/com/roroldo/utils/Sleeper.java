package com.roroldo.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 睡眠辅助类
 * @author 落霞不孤
 */
@Slf4j(topic = "c.Sleeper")
public class Sleeper {

    public static void sleep(long timeout) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            log.error("e {}", e);
        }
    }
}
