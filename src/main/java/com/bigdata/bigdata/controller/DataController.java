package com.bigdata.bigdata.controller;

import com.alibaba.fastjson.JSON;
import com.bigdata.bigdata.entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 2024/4/21
 */
@RestController
public class DataController {
    @Autowired
    RedisTemplate redisTemplate;

    @GetMapping("/test")
    public Result getTest() {
//        Map locationCount = redisTemplate.opsForHash().entries("LocationCount");
        Object sentiment_trend = redisTemplate.opsForValue().get("sentiment_trend");

        return Result.success(JSON.parseObject((String) sentiment_trend));

//        return Result.success(sentiment_trend);
    }

}
