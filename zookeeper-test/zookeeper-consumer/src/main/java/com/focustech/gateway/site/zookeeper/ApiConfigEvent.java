package com.focustech.gateway.site.zookeeper;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ApiConfigEvent {
    private final ApiConfigEvent.Type type;
    private final String path;
    private final ZkApiNodeData data;

    public static enum Type {
        ADDED,
        UPDATED,
        REMOVED;
    }
}
