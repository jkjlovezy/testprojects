package com.focustech.gateway.site.route;

import com.focustech.gateway.site.route.data.ApiNodeData;
import com.focustech.gateway.site.zookeeper.core.AbstractNodeHandler;
import com.focustech.gateway.site.zookeeper.core.NodeEvent;
import com.focustech.gateway.site.zookeeper.core.NodeEventListener;
import com.focustech.gateway.site.zookeeper.core.NodeHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ApiNodeChangeHandler extends AbstractNodeHandler<ApiNodeData> implements NodeHandler {
    @Autowired
    private DynamicRouteService dynamicRouteService;
    @Autowired
    private ApiHolder apiHolder;

    @Autowired
    private List<NodeEventListener<ApiNodeData>> nodeEventListeners;

    @Override
    public void doHandle(NodeEvent<ApiNodeData> nodeEvent) {
        log.debug("ApiNodeHandler receive nodeEvent,nodeEvent={}", nodeEvent);
        if (nodeEvent.getData() == null) {
            log.warn("ApiNodeHandler receive nodeEvent but no data; nodeEvent={}", nodeEvent);
            return;
        }
        switch (nodeEvent.getOperation()) {
            case ADDED:
                fireAddedNodeEvent(nodeEvent);
                break;
            case UPDATED:
                fireUpdatedNodeEvent(nodeEvent);
                break;
            case DELETED:
                fireDeletedNodeEvent(nodeEvent);
                break;
            default:
        }
    }

    private void fireAddedNodeEvent(NodeEvent<ApiNodeData> nodeEvent) {
        if (nodeEventListeners != null && nodeEventListeners.size() > 0) {
            nodeEventListeners.stream().forEach(listener -> listener.add(nodeEvent));
        }
    }

    private void fireUpdatedNodeEvent(NodeEvent<ApiNodeData> nodeEvent) {
        if (nodeEventListeners != null && nodeEventListeners.size() > 0) {
            nodeEventListeners.stream().forEach(listener -> listener.update(nodeEvent));
        }
    }

    private void fireDeletedNodeEvent(NodeEvent<ApiNodeData> nodeEvent) {
        if (nodeEventListeners != null && nodeEventListeners.size() > 0) {
            nodeEventListeners.stream().forEach(listener -> listener.delete(nodeEvent));
        }
    }


}
