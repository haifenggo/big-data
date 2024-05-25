package com.bigdata.bigdata.service;

import com.bigdata.bigdata.entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 2024/5/7
 */
@Component
@ServerEndpoint(value = "/websocket/{socketName}")
public class WebSocketServer {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    private static int onlineCount = 0;

    public static ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskSchedulerBean;

    @Autowired
    static ThreadPoolTaskScheduler threadPoolTaskScheduler;


    @PostConstruct
    public void init() {
        threadPoolTaskScheduler = threadPoolTaskSchedulerBean;
    }

    private static final Map<String, ScheduledFuture<?>> scheduledMap = new ConcurrentHashMap<>();

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    private String socketName;


    public static Boolean addSchedule(String taskName, Runnable task, Trigger trigger) {
        if (scheduledMap.containsKey(taskName)) {
            return false;
        }
        ScheduledFuture<?> schedule = threadPoolTaskScheduler.schedule(task, trigger);
        scheduledMap.put(taskName, schedule);
        return true;
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("socketName") String socketName) {
        this.session = session;
        this.socketName = socketName;
        if (webSocketMap.containsKey(socketName)) {
            webSocketMap.remove(socketName);
            webSocketMap.put(socketName, this);
        } else {
            webSocketMap.put(socketName, this);
            addOnlineCount();
        }
        sendMessage(socketName + "连接成功");
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(socketName)) {
            webSocketMap.remove(socketName);
            subOnlineCount();
        }
        if (scheduledMap.containsKey(socketName)) {
            ScheduledFuture<?> scheduledFuture = scheduledMap.get(socketName);
            if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
                scheduledFuture.cancel(true);
            }
            scheduledMap.remove(socketName);
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        this.sendMessage("收到信息：" + message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    /**
     * 发送消息的私有方法，需要调用配置中的session才能发送
     */
    private void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException ignored) {
        }
    }

    public static void sendMessage(@PathParam("socketName") String socketName, String message) {
        if (StringUtils.isNotBlank(message) && webSocketMap.containsKey(socketName)) {
            webSocketMap.get(socketName).sendMessage(message);
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}
