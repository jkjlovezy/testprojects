package com.focustech.gateway.site.zookeeper.core;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class NodeInfo<T extends NodeData> {
    private int dataVersion;
    private T data;

    public NodeInfo(int dataVersion, T data) {
        this.dataVersion = dataVersion;
        this.data = data;
    }
}
