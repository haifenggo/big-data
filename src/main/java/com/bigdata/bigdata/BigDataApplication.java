package com.bigdata.bigdata;

import com.bigdata.bigdata.flink.FlinkKafka;
import com.bigdata.bigdata.utils.CsvFileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BigDataApplication implements CommandLineRunner {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(BigDataApplication.class, args);
        FlinkKafka flinkJobComponent = context.getBean(FlinkKafka.class);
        flinkJobComponent.runJob();
    }

    @Autowired
    CsvFileReader csvFileReader;

    @Override
    public void run(String... args) {
        csvFileReader.read();
    }
}
