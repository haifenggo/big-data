package com.bigdata.bigdata.flink;

import com.alibaba.fastjson.JSON;
import com.bigdata.bigdata.entity.VideoData;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.runtime.ValueSerializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * 2024/4/9 21:52
 */
@Component
public class FlinkKafka {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void runJob() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "192.168.101.101:9092");
        properties.setProperty("group.id", "big-data-group");
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        FlinkKafkaConsumer<String> kafkaConsumer = new FlinkKafkaConsumer<>(
                "big-data-topic-test", // topic
                new SimpleStringSchema(), // schema
                properties // consumer config
        );

        DataStream<String> kafkaStream = env.addSource(kafkaConsumer);

        properties = new Properties();
        properties.setProperty("bootstrap.servers", "192.168.101.101:9092");

        kafkaStream
                .map(value -> JSON.parseObject(value, VideoData.class)) // 解析JSON字符串为VideoData对象
                .flatMap((FlatMapFunction<VideoData, Tuple2<String, Integer>>) (videoData, out) -> {
                    String publishLocation = videoData.getPublishLocation();
                    if (publishLocation != null && !publishLocation.isEmpty()) {
                        out.collect(new Tuple2<>(publishLocation, 1));
                    }
                }) // 根据发布地点进行统计
                .returns(Types.TUPLE(Types.STRING, Types.INT)) // 显式指定返回类型
                .keyBy(tuple -> tuple.f0)
                .sum(1) // 对每个发布地点的计数进行求和
                .map(tuple -> "{\"publishLocation\":\"" + tuple.f0 + "\",\"count\":" + tuple.f1 + "}") // 将Tuple转换为JSON字符串
                .addSink(new FlinkKafkaProducer<>(
                        "output-topic",
                        new SimpleStringSchema(), properties)); // 将结果发送到Kafka主题

        kafkaStream
                .map(value -> JSON.parseObject(value, VideoData.class)) // 解析JSON字符串为VideoData对象
                .flatMap((FlatMapFunction<VideoData, Tuple2<String, Integer>>) (videoData, out) -> {
                    String publishLocation = videoData.getPublishLocation();
                    if (publishLocation != null && !publishLocation.isEmpty()) {
                        out.collect(new Tuple2<>(publishLocation, 1));
                    }
                }) // 根据发布地点进行统计
                .returns(Types.TUPLE(Types.STRING, Types.INT)) // 显式指定返回类型
                .keyBy(tuple -> tuple.f0)
                .sum(1) // 对每个发布地点的计数进行求和
                .map(tuple -> "{\"publishLocation\":\"" + tuple.f0 + "\",\"count\":" + tuple.f1 + "}") // 将Tuple转换为JSON字符串
                .addSink(new FlinkKafkaProducer<>(
                        "big-data-topic-test-1",
                        new SimpleStringSchema(), properties)); // 将结果发送到Kafka主题


        System.out.println("flink start");
        env.execute("Flink");
    }

    public static void main(String[] args) throws Exception {
        FlinkKafka flinkKafka = new FlinkKafka();
        flinkKafka.runJob();
    }
}
