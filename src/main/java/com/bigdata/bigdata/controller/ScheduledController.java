package com.bigdata.bigdata.controller;

import com.alibaba.fastjson.JSON;
import com.bigdata.bigdata.entity.Result;
import com.bigdata.bigdata.service.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RestController
public class ScheduledController {

    @Autowired
    RedisTemplate redisTemplate;

    @GetMapping("/webSocketList")
    public Result webSocketList() {
        log.info("获取列表");
        List<String> webSocketList = new ArrayList<>();
        webSocketList.add("/websocket/locationCount");
        webSocketList.add("/websocket/sentiment_trend");
        webSocketList.add("/websocket/likes_trend");
        webSocketList.add("/websocket/video_top");
        webSocketList.add("/websocket/top_10");
        webSocketList.add("/websocket/lda_topics");
        return Result.success(webSocketList);
    }

    @GetMapping("/startLocationCount")
    public Result startLocationCountTask() {
        boolean added = WebSocketServer.addSchedule("locationCount", () -> {
            Map locationCount = redisTemplate.opsForHash().entries("LocationCount");
            WebSocketServer.sendMessage("locationCount", JSON.toJSONString(locationCount));
        }, new CronTrigger("0/3 * * * *  ?"));
        if (!added) {
            Result.fail("任务添加失败");
        }
        return Result.success();
    }

    @GetMapping("/startSentimentTrend")
    public Result get_sentiment_trend() {
        boolean added = WebSocketServer.addSchedule("sentiment_trend", () -> {
            String sentiment_trend = (String) redisTemplate.opsForValue().get("sentiment_trend");
            WebSocketServer.sendMessage("sentiment_trend", sentiment_trend);
        }, new CronTrigger("0/10 * * * *  ?"));
        if (!added) {
            Result.fail("任务添加失败");
        }
        return Result.success();
    }

    @GetMapping("/startLikesTrend")
    public Result get_likes_trend() {
        boolean added = WebSocketServer.addSchedule("likes_trend", () -> {
            String likes_trend = (String) redisTemplate.opsForValue().get("likes_trend");
            WebSocketServer.sendMessage("likes_trend", likes_trend);
        }, new CronTrigger("0/10 * * * *  ?"));
        if (!added) {
            Result.fail("任务添加失败");
        }
        return Result.success();
    }

    @GetMapping("/video_top")
    public Result get_video_top() {
        boolean added = WebSocketServer.addSchedule("video_top", () -> {
            Map videoTop = redisTemplate.opsForHash().entries("video_top");
            WebSocketServer.sendMessage("video_top", JSON.toJSONString(videoTop));
        }, new CronTrigger("0/10 * * * *  ?"));
        if (!added) {
            Result.fail("任务添加失败");
        }
        return Result.success();
    }


    @GetMapping("/top_10")
    public Result get_top_10() {
        boolean added = WebSocketServer.addSchedule("top_10", () -> {
            String top_10_views = (String) redisTemplate.opsForValue().get("top_10_views");
            String top_10_comments = (String) redisTemplate.opsForValue().get("top_10_comments");
            Map<String, Object> map = new HashMap<>();
            map.put("top_10_views", JSON.parse(top_10_views));
            map.put("top_10_comments", JSON.parse(top_10_comments));
            WebSocketServer.sendMessage("top_10", JSON.toJSONString(map));
        }, new CronTrigger("0/10 * * * *  ?"));
        if (!added) {
            Result.fail("任务添加失败");
        }
        return Result.success();
    }


    @GetMapping("/lda_topics")
    public Result get_lda_topics() {
        boolean added = WebSocketServer.addSchedule("lda_topics", () -> {
            String lda_topics = (String) redisTemplate.opsForValue().get("lda_topics");
            WebSocketServer.sendMessage("lda_topics", lda_topics);
        }, new CronTrigger("0/10 * * * *  ?"));
        if (!added) {
            Result.fail("任务添加失败");
        }
        return Result.success();
    }


}
