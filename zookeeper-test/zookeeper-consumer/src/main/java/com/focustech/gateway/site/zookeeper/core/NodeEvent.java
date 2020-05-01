package com.focustech.gateway.site.zookeeper.core;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class NodeEvent<T extends NodeData> {
    private NodeOperationType operation;
    private String path;
    private int dataVersion;
    private T data;

    public NodeEvent(NodeOperationType operation, String path, int dataVersion, T data) {
        this.operation = operation;
        this.path = path;
        this.dataVersion = dataVersion;
        this.data = data;
    }


}
