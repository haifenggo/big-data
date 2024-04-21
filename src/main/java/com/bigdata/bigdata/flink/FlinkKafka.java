package com.bigdata.bigdata.flink;

import com.alibaba.fastjson.JSON;
import com.bigdata.bigdata.entity.VideoData;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.runtime.ValueSerializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

/**
 * 2024/4/9 21:52
 */
@Component
public class FlinkKafka {

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
                .filter(videoData -> videoData.getPublishLocation() != null)
                .keyBy(VideoData::getPublishLocation)
                .window(SlidingProcessingTimeWindows.of(Time.seconds(20), Time.seconds(5)))
                .process(new ProcessWindowFunction<VideoData, String, String, TimeWindow>() {
                    @Override
                    public void process(String key, Context context, Iterable<VideoData> elements, Collector<String> out) {
                        Set<String> distinctBVs = new HashSet<>();
                        for (VideoData videoData : elements) {
                            distinctBVs.add(videoData.getBV());
                        }
                        Map<String, String > mp = new HashMap();
                        mp.put("publishLocation", key);
                        mp.put("count", String.valueOf(distinctBVs.size()));
                        mp.put("timestamp", Instant.now().toString());
                        String string = JSON.toJSONString(mp);
                        out.collect(string);
                    }
                })
                .addSink(new FlinkKafkaProducer<>(
                        "output-topic-LocationCount",
                        new SimpleStringSchema(), properties)); // 将结果发送到Kafka主题


        System.out.println("flink start");
        env.execute("Flink");
    }

    public static void main(String[] args) throws Exception {
        FlinkKafka flinkKafka = new FlinkKafka();
        flinkKafka.runJob();
    }
}
