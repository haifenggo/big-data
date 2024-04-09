package com.bigdata.bigdata.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

// 2024/3/19
@Service
public class KafkaConsumer {
    @KafkaListener(topics = "big-data-topic", groupId = "big-data-group")
    public void consume(String message) {
        System.out.println("Received message: " + message);
    }

}
