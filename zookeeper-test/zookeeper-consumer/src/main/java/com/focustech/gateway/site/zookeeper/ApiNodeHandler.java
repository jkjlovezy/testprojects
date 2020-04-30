package com.focustech.gateway.site.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class ApiNodeHandler {

    private ConcurrentLinkedQueue<ApiConfigEvent> queue = new ConcurrentLinkedQueue<ApiConfigEvent>();
    private AtomicBoolean isHandling = new AtomicBoolean(false);

    public void handle(ApiConfigEvent configEvent) {
        offer(configEvent);
        if (isHandling.compareAndSet(false, true)) {
            new ApiNodeHandler.ProcessThread().run();
        }

    }

    public class ProcessThread implements Runnable {
        @Override
        public void run() {
            ApiConfigEvent configEvent;
            try {
                while (true) {
                    if ((configEvent = queue.poll()) == null) {
                        break;
                    }
                    log.info("reset route info: ApiConfigEvent={}", configEvent);
                    switch (configEvent.getType()) {
                        case ADDED:
                            break;
                        case UPDATED:
                            break;
                        case REMOVED:
                            break;
                        default:
                    }
                }
            } catch (Exception e) {
                log.error("process error", e);
            } finally {
                isHandling.compareAndSet(true, false);
            }
        }
    }


    private void offer(ApiConfigEvent nodeData) {
        try {
            if (!queue.offer(nodeData))
                log.error("process ZkApiNodeData fail because of queue is full. ZkApiNodeData = {}", nodeData);
        } catch (Exception e) {
            log.error("offer error", e);
        }
    }

}
