package com.roroldo.pattern;

import com.roroldo.utils.Downloader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


/**
 * 生产者消费者模式
 *  情景：模拟消息队列。生产者用来产生消息到队列中，消费者从队列中取出消息消费。
 * @author 落霞不孤
 */
@Slf4j(topic = "c.ProducerAndConsumerTest")
public class ProducerAndConsumerTest {
    public static void main(String[] args) {
        MessageQueue messageQueue = new MessageQueue(2);
        // 4 个生产者
        for (int i = 0; i < 4; i++) {
            int id = i;
            new Thread(() -> {
                log.debug("生产消息...");
                try {
                    List<String> msg = Downloader.download();
                    log.debug("try put message({})", id);
                    messageQueue.put(new Message(id, msg));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, "生产者" + id).start();
        }

        // 1 个消费者
        new Thread(() -> {
            while (true) {
                Message message = messageQueue.take();
                List<String> detail = (List<String>) message.getMsg();
                log.debug("take message({}): [{}] lines", message.getId(), detail.size());
            }

        }, "消费者").start();
    }
}


/**
 * 消息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
class Message {
    private Integer id;
    private Object msg;
}


/**
 * 消息队列
 */
@Slf4j(topic = "c.MessageQueue")
class MessageQueue {
    private LinkedList<Message> queue;
    private int capacity;

    public MessageQueue(int capacity) {
        this.capacity = capacity;
        queue = new LinkedList<>();
    }

    /**
     * 消费消息
     * @return Message
     */
    public Message take() {
        synchronized (queue) {
            // 没有消息
            while (queue.isEmpty()) {
                log.debug("{} 等待消息中....", this);
                try {
                    queue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // 消费消息，通知生产者
            Message message = queue.removeFirst();
            queue.notifyAll();
            return message;
        }
    }

    /**
     * 存放消息
     * @param message message
     */
    public void put(Message message) {
        synchronized (queue) {
            // 队列容量已满
            while (queue.size() == capacity) {
                log.debug("消息队列容量已满，等待中....");
                try {
                    queue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // 存入消息，通知消费者
            queue.addLast(message);
            queue.notifyAll();
        }
    }
}









