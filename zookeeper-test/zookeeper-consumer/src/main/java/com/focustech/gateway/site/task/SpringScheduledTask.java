package com.focustech.gateway.site.task;

import com.focustech.gateway.site.zookeeper.ZookeeperClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class SpringScheduledTask {
    @Autowired
    ZookeeperClient zookeeperClient;

    @Scheduled(cron = "0/15 * * * * ?")
    public void refreshApi() {
        log.info("--refresh api start");
        try {
            Map<String, String> apiMap = zookeeperClient.getAllNodeData();
            apiMap.forEach((key, value) -> log.info("--refresh api ,key = {},value= {}", key, value));
        } catch (Exception e) {
            log.info("--refresh api  error:", e);
        }
    }
}
