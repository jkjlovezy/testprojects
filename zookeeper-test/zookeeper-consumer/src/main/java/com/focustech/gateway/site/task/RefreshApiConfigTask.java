package com.focustech.gateway.site.task;

import com.focustech.gateway.site.zookeeper.apinode.ApiNodeProcessor;
import com.focustech.gateway.site.zookeeper.core.ZookeeperClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RefreshApiConfigTask {
    @Autowired
    ZookeeperClient zookeeperClient;

    @Autowired
    ApiNodeProcessor apiNodeProcessor;

    @Scheduled(cron = "0/15 * * * * ?")
    public void refreshNodes() {
        log.info("--refresh nodes, start");
        try {
//            List<String> allNodePathList = zookeeperClient.getAllNode();
//            log.info("--refresh nodes, all path: ");
//            allNodePathList.forEach((path) -> log.info("--refresh nodes, path={}", path));
//            apiConfigHolder.compareAndRemoveApi(allNodePathList);
        } catch (Exception e) {
            log.info("--refresh nodes,  error:", e);
        }
    }
}
