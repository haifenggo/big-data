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

    @GetMapping("/locationcount")
    public Result getLocationCount(){
        Map locationCount = redisTemplate.opsForHash().entries("LocationCount");
//        String string = JSON.toJSONString(locationCount);
        return Result.success(locationCount);
    }

}
