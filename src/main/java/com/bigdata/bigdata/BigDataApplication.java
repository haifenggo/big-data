package com.bigdata.bigdata;

import com.bigdata.bigdata.jobs.FlinkJobComponent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class BigDataApplication {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(BigDataApplication.class, args);
        FlinkJobComponent flinkJobComponent = context.getBean(FlinkJobComponent.class);
        flinkJobComponent.runFlinkJob();
    }

}
