package com.bigdata.bigdata.task;

import com.alibaba.fastjson.JSON;
import com.bigdata.bigdata.entity.VideoData;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.*;

/**
 * 2024/5/24
 */
public class VideoTopTask {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "192.168.101.101:9092");
        properties.setProperty("group.id", "video-top");
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        FlinkKafkaConsumer<String> kafkaConsumer = new FlinkKafkaConsumer<>(
                "board", // topic
                new SimpleStringSchema(), // schema
                properties // consumer config
        );

        DataStream<String> kafkaStream = env.addSource(kafkaConsumer);

        properties = new Properties();
        properties.setProperty("bootstrap.servers", "192.168.101.101:9092");


        kafkaStream
                .map(value -> JSON.parseObject(value, VideoData.class))
                .keyBy((KeySelector<VideoData, String>) videoData -> videoData.getBoard())
                .window(SlidingProcessingTimeWindows.of(Time.minutes(1), Time.seconds(10))) // 滑动窗口示例，每1分钟滑动一次，每次计算10秒的数据
                .reduce((ReduceFunction<VideoData>) (videoData1, videoData2) -> {
                    Double score1 = calculateScore(videoData1);
                    Double score2 = calculateScore(videoData2);
                    return score1 > score2 ? videoData1 : videoData2;
                })

                .map(videoData -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("board", videoData.getBoard());
                    map.put("bvid", videoData.getBvid());
                    map.put("score", String.valueOf(calculateScore(videoData)));
                    map.put("title", videoData.getTitle());
                    map.put("url", "www.bilibili.com/" + videoData.getBvid());
                    return JSON.toJSONString(map);
                })
                .addSink(new FlinkKafkaProducer<>(
                        "video-top-topic",
                        new SimpleStringSchema(), properties))
                .name("video-top-sink");
        System.out.println("flink VideoTopTask start");
        env.execute("VideoTopTask");
    }


    private static Double calculateScore(VideoData videoData) {
        return videoData.getViews() * 1.0 +
                videoData.getLikes() * 1.1 +
                videoData.getFavorites() * 1.2 +
                videoData.getComments() * 1.3 +
                videoData.getCoins() * 1.4 +
                videoData.getShares() * 1.5;
    }

}

