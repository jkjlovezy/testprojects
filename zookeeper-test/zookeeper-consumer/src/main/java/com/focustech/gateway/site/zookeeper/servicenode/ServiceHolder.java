package com.focustech.gateway.site.zookeeper.servicenode;

import com.focustech.gateway.site.zookeeper.core.NodeEventListener;
import com.focustech.gateway.site.zookeeper.core.NodeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(1)
public class ServiceHolder implements NodeEventListener<ServiceNodeData> {

    @Override
    public void add(NodeEvent<ServiceNodeData> nodeEvent) {
        log.debug("ServiceHolder add node event:{}", nodeEvent);
    }

    @Override
    public void update(NodeEvent<ServiceNodeData> nodeEvent) {
        log.debug("ServiceHolder update node event:{}", nodeEvent);
    }

    @Override
    public void delete(NodeEvent<ServiceNodeData> nodeEvent) {
        log.debug("ServiceHolder delete node event:{}", nodeEvent);
    }


}
