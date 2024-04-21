package com.bigdata.bigdata.utils;

import com.alibaba.fastjson.JSON;
import com.bigdata.bigdata.entity.VideoData;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;

/**
 * 2024/4/10
 */
@Component
public class CsvFileReader {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


    public void read() {
        try (CSVReader fileReader = new CSVReader(new InputStreamReader(new FileInputStream("C:\\Users\\张海锋\\Desktop\\作业\\大数据项目/all_data.csv"), "gbk"))) {
            HeaderColumnNameMappingStrategy<VideoData> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(VideoData.class);

            CsvToBean<VideoData> csvToBean = new CsvToBeanBuilder<VideoData>(fileReader)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withSkipLines(0)
                    .build();


            List<VideoData> videoDataList = csvToBean.parse();
            new Thread(() -> {
                Random random = new Random();
                for (VideoData videoData : videoDataList) {
                    String videoDataJson = JSON.toJSONString(videoData);
                    kafkaTemplate.send("big-data-topic-test", videoDataJson);
//                    System.out.println("video send: " + videoDataJson);
                    // 随机暂停时间，范围是0到3000毫秒
                    int sleepTime = random.nextInt(3000);
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        CsvFileReader csvFileReader = new CsvFileReader();
        csvFileReader.read();
    }
}
