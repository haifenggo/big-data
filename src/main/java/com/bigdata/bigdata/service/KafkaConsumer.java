package com.bigdata.bigdata.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bigdata.bigdata.entity.Comment;
import com.bigdata.bigdata.entity.VideoData;
import lombok.val;
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
        String location = (String) locationCount.get("publishLocation");
        String count = (String) locationCount.get("count");
        String timestamp = (String) locationCount.get("timestamp");
        Map<String, String> mp = new HashMap();
        mp.put("count", count);
        mp.put("timestamp", timestamp);
//        redisTemplate.opsForHash().put("LocationCount", location, JSON.toJSONString(mp));
        redisTemplate.opsForHash().put("LocationCount", location, JSON.toJSON(mp));
//        System.out.println("Received message: " + message);
    }

    @KafkaListener(topics = "big-data-topic-test", groupId = "output-group")
    public void consume_test(String message) {
        System.out.println("Received message: " + message);
    }


    @KafkaListener(topics = "board", groupId = "python-consumer")
    public void consume_board(String message) {
        mongoTemplate.insert(JSON.parseObject(message, VideoData.class));
//        System.out.println("Received board: " + videoData);
    }

    @KafkaListener(topics = "comment", groupId = "python-consumer")
    public void consume_comments(String message) {
        mongoTemplate.insert(JSON.parseObject(message, Comment.class));
//        System.out.println("Received message: " + comment);
    }


    @KafkaListener(topics = "video-top-topic", groupId = "video-top-consumer")
    public void consume_video_top(String message) {
        Map videoData = JSON.parseObject(message, Map.class);
        redisTemplate.opsForHash().put("video_top", videoData.get("board"), videoData);
    }
}
