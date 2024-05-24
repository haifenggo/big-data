package com.bigdata.bigdata.task;

import com.alibaba.fastjson.JSON;
import com.bigdata.bigdata.entity.VideoData;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.time.Instant;
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
                .aggregate(new AggregateFunction<VideoData, Tuple4<String, Double, String, String>, Tuple4<String, Double, String, String>>() {
                    @Override
                    public Tuple4<String, Double, String, String> createAccumulator() {
                        return new Tuple4<>("", 0.0, "", "");
                    }

                    @Override
                    public Tuple4<String, Double, String, String> add(VideoData value, Tuple4<String, Double, String, String> accumulator) {
                        Double score = value.getViews() * 1.0 +
                                value.getLikes() * 1.1 +
                                value.getCoins() * 1.2 +
                                value.getShares() * 1.3 +
                                value.getComments() * 1.4;
                        if (score > accumulator.f1) {
                            return new Tuple4<>(value.getBvid(), score, value.getTitle(), "url");
                        } else {
                            return accumulator;
                        }
                    }

                    @Override
                    public Tuple4<String, Double, String, String> getResult(Tuple4<String, Double, String, String> accumulator) {
                        return accumulator;
                    }

                    @Override
                    public Tuple4<String, Double, String, String> merge(Tuple4<String, Double, String, String> a, Tuple4<String, Double, String, String> b) {
                        if (a.f1 > b.f1) {
                            return a;
                        } else {
                            return b;
                        }
                    }
                })
                .map(value -> {
                            Map<String, String> map = new HashMap<>();
                            map.put("bvid", value.f0);
                            map.put("score", value.f1.toString());
                            map.put("title", value.f2);
                            map.put("url", value.f3);
                            return JSON.toJSONString(map);
                        }
                ).addSink(new FlinkKafkaProducer<>(
                        "video-top-topic",
                        new SimpleStringSchema(), properties))
                .name("video-top-sink");
        System.out.println("flink VideoTopTask start");
        env.execute("VideoTopTask");
    }
}

