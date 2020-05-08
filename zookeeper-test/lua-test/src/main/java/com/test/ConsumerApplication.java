package com.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.test"})
@EnableScheduling
public class ConsumerApplication {
    public static void main(String[] args) {
        new SpringApplication(ConsumerApplication.class).run(args);
    }

}
