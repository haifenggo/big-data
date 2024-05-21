package com.bigdata.bigdata.controller;

import com.alibaba.fastjson.JSON;
import com.bigdata.bigdata.entity.Result;
import com.bigdata.bigdata.service.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 2024/5/8
 */
@RestController
public class ScheduledController {
    @Autowired
    ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    RedisTemplate redisTemplate;

    private static final Map<String, ScheduledFuture<?>> scheduledMap = new ConcurrentHashMap<>();

    @GetMapping("/startLocationCountTask")
    public Result startLocationCountTask() {
        if (scheduledMap.containsKey("locationCountTask")) {
            return Result.fail("任务已存在");
        }
        String corn = "0/2 * * * *  ?";
        Runnable locationCountTask = () -> {
            Map locationCount = redisTemplate.opsForHash().entries("LocationCount");
            WebSocketServer.sendMessage("locationCount", JSON.toJSONString(locationCount));
        };
        ScheduledFuture<?> schedule = threadPoolTaskScheduler.schedule(locationCountTask, new CronTrigger(corn));
        scheduledMap.put("locationCountTask", schedule);
        return Result.success("/websocket/locationCount");
    }

    @GetMapping("/cancelLocationCountTask")
    public Result cancelLocationCountTask() {
        ScheduledFuture<?> scheduledFuture = scheduledMap.get("locationCountTask");
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(true);
        }
        scheduledMap.remove("locationCountTask");
        return Result.success("关闭成功");
    }
}
