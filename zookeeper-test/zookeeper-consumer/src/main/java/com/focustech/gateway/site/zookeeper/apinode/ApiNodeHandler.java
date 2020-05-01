package com.focustech.gateway.site.zookeeper.apinode;

import com.focustech.gateway.site.zookeeper.core.AbstractNodeHandler;
import com.focustech.gateway.site.zookeeper.core.NodeEvent;
import com.focustech.gateway.site.zookeeper.core.NodeHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApiNodeHandler extends AbstractNodeHandler<ApiNodeData> implements NodeHandler {

    @Override
    public void doHandle(NodeEvent<ApiNodeData> nodeEvent) {
        log.debug("ApiNodeHandler receive nodeEvent,nodeEvent={}", nodeEvent);
        switch (nodeEvent.getOperation()) {
            case ADDED:
                break;
            case UPDATED:
                break;
            case REMOVED:
                break;
            default:
        }
    }
}
