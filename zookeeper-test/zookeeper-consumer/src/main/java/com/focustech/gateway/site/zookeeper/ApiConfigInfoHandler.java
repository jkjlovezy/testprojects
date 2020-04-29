package com.focustech.gateway.site.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class ApiConfigInfoHandler {
    private ConcurrentLinkedQueue<ApiZkNodeData> queue = new ConcurrentLinkedQueue<ApiZkNodeData>();
    private AtomicBoolean isHandling = new AtomicBoolean(false);

    @PostConstruct
    public void init() {

    }

    public void handle(ApiConfigEvent configEvent) {
        switch (configEvent.getType()) {
            case ADDED:
                offer(configEvent.getData());
                if (isHandling.compareAndSet(false, true)) {
                    new ProcessThread().run();
                }
                break;
            case UPDATED:
                break;
            case REMOVED:
                break;
            default:
        }
    }

    public class ProcessThread implements Runnable {
        @Override
        public void run() {
            ApiZkNodeData apiInfo = null;
            try {
                while (true) {
                    if ((apiInfo = queue.poll()) == null) {
                        break;
                    }
                    log.info("trigger route config for the new apiInfo={}",apiInfo);
                }
            } catch (Exception e) {
                log.error("process error", e);
            } finally {
                isHandling.compareAndSet(true, false);
            }
        }
    }


    private void offer(ApiZkNodeData nodeData) {
        try {
            if (!queue.offer(nodeData))
                log.error("process apiZkNodeData fail because of queue is full. apiZkNodeData = {}", nodeData);
        } catch (Exception e) {
            log.error("offer error", e);
        }
    }
}
