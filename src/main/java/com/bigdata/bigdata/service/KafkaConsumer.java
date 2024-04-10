package com.bigdata.bigdata.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

// 2024/3/19
@Service
public class KafkaConsumer {
//    @KafkaListener(topics = "big-data-topic", groupId = "big-data-group")
//    public void consume(String message) {
//        System.out.println("Received message: " + message);
//    }

    @KafkaListener(topics = "output-topic", groupId = "output-group")
    public void consume(String message) {
        System.out.println("Received message: " + message);
    }

    /**
     * topics是Kafka中的消息分类。生产者将消息发送到特定的主题，消费者则从特定的主题中读取消息。
     * 一个主题可以被多个消费者订阅。
     * groupId是消费者组的标识。
     * 在Kafka中，消费者组是一种可以将消费者进行逻辑分组的方式。
     * 同一个消费者组内的消费者可以订阅一个或多个主题，
     * 并且Kafka会保证每个消息只会被消费者组内的一个消费者消费。
     * 这样可以实现负载均衡和容错。
     */
}
