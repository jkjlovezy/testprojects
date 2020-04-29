package com.focustech.gateway.site;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class ZookeeperConsumerApplication {
    public static void main(String[] args) {
        new SpringApplication(ZookeeperConsumerApplication.class).run(args);
    }

}
