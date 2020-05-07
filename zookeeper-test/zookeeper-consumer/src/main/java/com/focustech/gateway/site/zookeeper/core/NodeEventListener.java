package com.focustech.gateway.site.zookeeper.core;

public interface NodeEventListener<DATA extends NodeData> {
    void add(NodeEvent<DATA> nodeEvent);

    void update(NodeEvent<DATA> nodeEvent);

    void delete(NodeEvent<DATA> nodeEvent);
}
