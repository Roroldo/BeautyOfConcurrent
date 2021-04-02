package com.roroldo.pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Immutable 模式
 * @author 落霞不孤
 */
@Slf4j(topic = "c.ImmutableTest")
public class ImmutableTest {
    public static void main(String[] args) {
        Person person = new Person("roroldo", "广东");
        PrintPersonThread p1 = new PrintPersonThread("p1", person);
        PrintPersonThread p2 = new PrintPersonThread("p2", person);
        PrintPersonThread p3 = new PrintPersonThread("p3", person);

        p1.start();
        p2.start();
        p3.start();
    }
}


@AllArgsConstructor
@Getter
@ToString
final class Person {
    private final String name;
    private final String address;
}

@Slf4j(topic = "c.PrintPersonThread")
class PrintPersonThread extends Thread {
    private Person person;

    public PrintPersonThread(String name, Person person) {
        super(name);
        this.person = person;
    }

    @Override
    public void run() {
        while (true) {
            log.info(Thread.currentThread().getName() + " prints " + person);
        }
    }
}

