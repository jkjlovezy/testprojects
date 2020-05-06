package com.focustech.gateway.site.route.data;

import com.focustech.gateway.site.zookeeper.core.NodeData;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ApiNodeInfo<T extends NodeData> {
    private int dataVersion;
    private T data;

    public ApiNodeInfo(int dataVersion, T data) {
        this.dataVersion = dataVersion;
        this.data = data;
    }
}
