package com.focustech.gateway.site.zookeeper.core;

public interface NodeHandler<T extends NodeData> {
    void handle(NodeEvent<T> nodeEvent);
}
