package com.bigdata.bigdata;

import com.bigdata.bigdata.task.LocationCountTask;
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
//        LocationCountTask flinkJobComponent = context.getBean(LocationCountTask.class);
//        flinkJobComponent.runJob();
    }


    @Override
    public void run(String... args) {
//        csvFileReader.read();
    }
}
