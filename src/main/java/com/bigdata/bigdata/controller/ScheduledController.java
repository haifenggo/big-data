package com.bigdata.bigdata.controller;

import com.alibaba.fastjson.JSON;
import com.bigdata.bigdata.entity.Result;
import com.bigdata.bigdata.service.WebSocketServer;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 2024/5/8
 */
@RestController
public class ScheduledController {

    @Autowired
    RedisTemplate redisTemplate;

    @GetMapping("/webSocketList")
    public Result webSocketList() {
        List<String> webSocketList = new ArrayList<>();
        webSocketList.add("/websocket/locationCount");
        webSocketList.add("/websocket/sentiment_trend");
        webSocketList.add("/websocket/likes_trend");
        return Result.success(webSocketList);
    }

    @GetMapping("/startLocationCount")
    public Result startLocationCountTask() {
        boolean added = WebSocketServer.addSchedule("locationCount", () -> {
            Map locationCount = redisTemplate.opsForHash().entries("LocationCount");
            WebSocketServer.sendMessage("locationCount", JSON.toJSONString(locationCount));
        }, new CronTrigger("0/2 * * * *  ?"));
        if (!added) {
            Result.fail("任务添加失败");
        }
        return Result.success();
    }

    @GetMapping("/startSentimentTrend")
    public Result get_sentiment_trend() {
        boolean added = WebSocketServer.addSchedule("sentiment_trend", () -> {
            Map sentiment_trend = redisTemplate.opsForHash().entries("sentiment_trend");
            WebSocketServer.sendMessage("sentiment_trend", JSON.toJSONString(sentiment_trend));
        }, new CronTrigger("0/2 * * * *  ?"));
        if (!added) {
            Result.fail("任务添加失败");
        }
        return Result.success();
    }

    @GetMapping("/startLikesTrend")
    public Result get_likes_trend() {
        boolean added = WebSocketServer.addSchedule("likes_trend", () -> {
            Map likes_trend = redisTemplate.opsForHash().entries("likes_trend");
            WebSocketServer.sendMessage("likes_trend", JSON.toJSONString(likes_trend));
        }, new CronTrigger("0/2 * * * *  ?"));
        if (!added) {
            Result.fail("任务添加失败");
        }
        return Result.success();
    }

}
