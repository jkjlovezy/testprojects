package com.focustech.gateway.site.task;

import com.focustech.gateway.site.route.ApiHolder;
import com.focustech.gateway.site.route.ApiNodeProcessCenter;
import com.focustech.gateway.site.zookeeper.core.ZookeeperClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;

@Slf4j
//@Component
public class RefreshApiConfigTask {
    @Autowired
    ZookeeperClient zookeeperClient;

    @Autowired
    ApiNodeProcessCenter apiNodeProcessCenter;
    @Autowired
    ApiHolder apiHolder;

    @Scheduled(cron = "0/15 * * * * ?")
    public void synchronizeApiNodeInfo() {
        long startTime = System.currentTimeMillis();
        log.info("---synchronize Api node info start.currentTime={}", LocalDateTime.now());
        StopWatch stopwatch = null;
        try {
            log.info("apiHolder.apiMap = {}",apiHolder.getApi());
            apiNodeProcessCenter.synchronizeNodeInfo();
            log.info("---synchronize Api node info end.currentTime={}, consumingTime={}ms", LocalDateTime.now(), System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.info("---synchronize Api node info error.currentTime={}, consumingTime={}ms, exception:", LocalDateTime.now(), System.currentTimeMillis() - startTime, e);
        }
    }
}
