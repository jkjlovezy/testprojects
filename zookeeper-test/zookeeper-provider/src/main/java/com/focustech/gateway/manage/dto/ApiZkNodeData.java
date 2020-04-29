package com.focustech.gateway.manage.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiZkNodeData {
    private String serviceDomain;
    private String servicePath;
    private Integer requestTimeout;
    private Integer authentStrategy;
    private Integer flowlimitEnable;
    private Integer ruleGroupId;
}
