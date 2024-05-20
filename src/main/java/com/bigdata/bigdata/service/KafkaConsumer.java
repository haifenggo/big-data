package com.bigdata.bigdata.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bigdata.bigdata.entity.Comment;
import com.bigdata.bigdata.entity.VideoData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

// 2024/3/19
@Service
public class KafkaConsumer {
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private MongoTemplate mongoTemplate;

    @KafkaListener(topics = "locationCount-topic", groupId = "locationCount-consume-group")
    public void consumeLocationCount(String message) {
        JSONObject locationCount = JSON.parseObject(message);
        String location = (String)locationCount.get("publishLocation");
        String count = (String)locationCount.get("count");
        String timestamp = (String)locationCount.get("timestamp");
        Map<String, String > mp = new HashMap();
        mp.put("count", count);
        mp.put("timestamp", timestamp);
        redisTemplate.opsForHash().put("LocationCount", location, mp);
//        System.out.println("Received message: " + message);
    }

    @KafkaListener(topics = "big-data-topic-test", groupId = "output-group")
    public void consume_test(String message) {
        System.out.println("Received message: " + message);
    }


    @KafkaListener(topics = "board", groupId = "python-consumer")
    public void consume_board(String message) {
        VideoData videoData = JSON.parseObject(message, VideoData.class);
        mongoTemplate.insert(videoData);
        System.out.println("Received board: " + videoData);
    }

    @KafkaListener(topics = "comments", groupId = "python-consumer")
    public void consume_comments(String message) {
        Comment comment = JSON.parseObject(message, Comment.class);
        mongoTemplate.insert(comment);
        System.out.println("Received message: " + comment);
    }
}
