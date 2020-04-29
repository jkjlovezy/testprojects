package com.focustech.gateway.site.zookeeper;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiConfigEvent {
    private final ApiConfigEvent.Type type;
    private final String path;
    private final ApiZkNodeData data;

    public static enum Type {
        ADDED,
        UPDATED,
        REMOVED;
    }
}
