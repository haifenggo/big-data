package com.bigdata;


import com.alibaba.fastjson.JSON;
import com.bigdata.entity.VideoData;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;

import java.time.Instant;
import java.util.*;

/**
 * 2024/4/9 21:52
 */
public class LocationCountTask {

    public void runJob() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "192.168.101.101:9092");
        properties.setProperty("group.id", "video-locationCount");
        properties.setProperty("auto.offset.reset", "earliest");
        FlinkKafkaConsumer<String> kafkaConsumer = new FlinkKafkaConsumer<>(
                "board", // topic
                new SimpleStringSchema(), // schema
                properties // consumer config
        );

        DataStream<String> kafkaStream = env.addSource(kafkaConsumer);

        properties = new Properties();
        properties.setProperty("bootstrap.servers", "192.168.101.101:9092");

        kafkaStream
                .map(value -> JSON.parseObject(value, VideoData.class)) // 解析JSON字符串为VideoData对象
                .filter(videoData -> videoData.getPubLocation() != null)
                .keyBy(VideoData::getPubLocation)
                .window(SlidingProcessingTimeWindows.of(Time.seconds(20), Time.seconds(5)))
                .process(new ProcessWindowFunction<VideoData, String, String, TimeWindow>() {
                    @Override
                    public void process(String key, Context context, Iterable<VideoData> elements, Collector<String> out) {
                        Set<String> distinctBVs = new HashSet<>();
                        for (VideoData videoData : elements) {
                            distinctBVs.add(videoData.getBvid());
                        }
                        Map<String, String> mp = new HashMap();
                        mp.put("publishLocation", key);
                        mp.put("count", String.valueOf(distinctBVs.size()));
                        mp.put("timestamp", String.valueOf(Instant.now().toEpochMilli()));
                        String string = JSON.toJSONString(mp);
                        out.collect(string);
                    }
                })
                .addSink(new FlinkKafkaProducer<>(
                        "locationCount-topic",
                        new SimpleStringSchema(), properties))
                .name("LocationCountKafkaSink"); // 将结果发送到Kafka主题


        System.out.println("flink start");
        env.execute("Flink");
    }

    public static void main(String[] args) throws Exception {
        LocationCountTask flinkKafka = new LocationCountTask();
        flinkKafka.runJob();
    }
}
