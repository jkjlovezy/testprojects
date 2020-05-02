package com.focustech.gateway.site.zookeeper.core;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public abstract class AbstractNodeHandler<T extends NodeData> implements NodeHandler {

    private ConcurrentLinkedQueue<NodeEvent<T>> queue = new ConcurrentLinkedQueue<NodeEvent<T>>();
    private AtomicBoolean isHandling = new AtomicBoolean(false);

    public void handle(NodeEvent nodeEvent) {
        offer(nodeEvent);
        if (isHandling.compareAndSet(false, true)) {
            new AbstractNodeHandler.ProcessThread().run();
        }

    }

    public abstract void doHandle(NodeEvent<T> nodeEvent);

    public class ProcessThread implements Runnable {
        @Override
        public void run() {
            NodeEvent<T> nodeEvent;
            try {
                while (true) {
                    if ((nodeEvent = queue.poll()) == null) {
                        break;
                    }
                    doHandle(nodeEvent);
                }
            } catch (Exception e) {
                log.error("process error", e);
            } finally {
                isHandling.compareAndSet(true, false);
            }
        }
    }


    private void offer(NodeEvent<T> nodeEvent) {
        try {
            if (!queue.offer(nodeEvent))
                log.error("process ZkApiNodeData fail because of queue is full. ZkApiNodeData = {}", nodeEvent);
        } catch (Exception e) {
            log.error("offer error", e);
        }
    }

}
