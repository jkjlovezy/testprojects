package com.focustech.gateway.site.zookeeper.servicenode;

import com.focustech.gateway.site.zookeeper.core.NodeEventListener;
import com.focustech.gateway.site.zookeeper.core.AbstractNodeHandler;
import com.focustech.gateway.site.zookeeper.core.NodeEvent;
import com.focustech.gateway.site.zookeeper.core.NodeHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ServiceNodeChangeHandler extends AbstractNodeHandler<ServiceNodeData> implements NodeHandler {
    @Autowired
    private List<NodeEventListener<ServiceNodeData>> nodeEventListeners;

    @Override
    public void doHandle(NodeEvent<ServiceNodeData> nodeEvent) {
        log.debug("ServiceNodeHandler receive nodeEvent,nodeEvent={}", nodeEvent);
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

    private void fireAddedNodeEvent(NodeEvent<ServiceNodeData> nodeEvent) {
        if (nodeEventListeners != null && nodeEventListeners.size() > 0) {
            nodeEventListeners.stream().forEach(listener -> listener.add(nodeEvent));
        }
    }

    private void fireUpdatedNodeEvent(NodeEvent<ServiceNodeData> nodeEvent) {
        if (nodeEventListeners != null && nodeEventListeners.size() > 0) {
            nodeEventListeners.stream().forEach(listener -> listener.update(nodeEvent));
        }
    }

    private void fireDeletedNodeEvent(NodeEvent<ServiceNodeData> nodeEvent) {
        if (nodeEventListeners != null && nodeEventListeners.size() > 0) {
            nodeEventListeners.stream().forEach(listener -> listener.delete(nodeEvent));
        }
    }
}
