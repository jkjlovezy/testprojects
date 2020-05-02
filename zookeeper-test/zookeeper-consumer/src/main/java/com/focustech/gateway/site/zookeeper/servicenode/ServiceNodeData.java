package com.focustech.gateway.site.zookeeper.servicenode;

import com.focustech.gateway.site.zookeeper.core.NodeData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ServiceNodeData  extends NodeData {
    private String className;
    private String metadata;
}
