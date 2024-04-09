package com.bigdata.bigdata.jobs;

import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.flink.streaming.api.windowing.time.Time;


// 2024/3/19
public class FlinkWindowExample {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<String> kafkaStream = env.addSource(new SourceFunction<String>() {
            @Override
            public void run(SourceContext<String> ctx) throws Exception {
                for (int i = 0; i < 100000; i++) {
                    ctx.collect(Integer.toString(i));
                }
            }

            @Override
            public void cancel() {

            }
        });
        DataStream<String> windowedStream = kafkaStream
                .keyBy((KeySelector<String, String>) value -> value)
                .timeWindow(Time.seconds(1))
                .process(new ProcessWindowFunction<String, String, String, TimeWindow>() {
                    @Override
                    public void process(String key, Context context, Iterable<String> elements, Collector<String> out) throws Exception {
                        for (String element : elements) {
                            out.collect(element);
                        }
                    }
                });


        windowedStream.print();

        env.execute("FlinkWindowExample");
    }
}
