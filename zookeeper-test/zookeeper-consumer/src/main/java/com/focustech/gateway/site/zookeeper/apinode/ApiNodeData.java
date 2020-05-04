package com.focustech.gateway.site.zookeeper.apinode;

import com.focustech.gateway.site.zookeeper.core.NodeData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ApiNodeData extends NodeData {
    private String id;
    private String serviceDomain;
    private String servicePath;
    private Integer requestTimeout;
    private Integer authentStrategy;
    private Integer flowlimitEnable;
    private Integer ruleGroupId;
    private String routeType;

}
