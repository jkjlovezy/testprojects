package com.focustech.gateway.site.zookeeper.servicenode;

import com.focustech.gateway.site.zookeeper.core.AbstractNodeHandler;
import com.focustech.gateway.site.zookeeper.core.NodeEvent;
import com.focustech.gateway.site.zookeeper.core.NodeHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ServiceNodeHandler extends AbstractNodeHandler<ServiceNodeData> implements NodeHandler {

    @Override
    public void doHandle(NodeEvent<ServiceNodeData> nodeEvent) {
        log.debug("ServiceNodeHandler receive nodeEvent,nodeEvent={}", nodeEvent);
        switch (nodeEvent.getOperation()) {
            case ADDED:
                break;
            case UPDATED:
                break;
            case DELETED:
                break;
            default:
        }
    }
}
