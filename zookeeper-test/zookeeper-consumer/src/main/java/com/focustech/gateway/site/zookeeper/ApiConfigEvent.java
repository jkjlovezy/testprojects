package com.focustech.gateway.site.zookeeper;

import lombok.Data;
import lombok.ToString;
import com.focustech.gateway.site.zookeeper.ZookeeperClient.Operation;

@Data
@ToString
public class ApiConfigEvent {
    private final Operation type;
    private final String path;
    private final ZkApiNodeData data;

    public static enum Type {
        ADDED,
        UPDATED,
        REMOVED;
    }
}
