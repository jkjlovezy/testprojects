package com.focustech.gateway.site.zookeeper;

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
public class ZkApiNodeData {
    private String serviceDomain;
    private String servicePath;
    private Integer requestTimeout;
    private Integer authentStrategy;
    private Integer flowlimitEnable;
    private Integer ruleGroupId;
}
