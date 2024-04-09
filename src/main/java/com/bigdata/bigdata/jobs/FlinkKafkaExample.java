package com.bigdata.bigdata.jobs;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

// 2024/3/19
public class FlinkKafkaExample {
    public static void main(String[] args) throws Exception {
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 创建一个远程环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createRemoteEnvironment(
                "192.168.101.101", // JobManager的主机名
                8081 // JobManager的端口
        );

        DataStream<String> kafkaStream = env.addSource(new SourceFunction<String>() {
            @Override
            public void run(SourceContext<String> ctx) throws Exception {
                for (int i = 0; i < 100; i++) {
                    ctx.collect(Integer.toString(i));
                }
            }

            @Override
            public void cancel() {

            }
        });

        kafkaStream.print();

        env.execute("Flink");
    }
}
