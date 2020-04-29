package com.focustech.gateway.manage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class ZookeeperProviderApplication {
    public static void main(String[] args) {
        new SpringApplication(ZookeeperProviderApplication.class).run(args);
    }

}
