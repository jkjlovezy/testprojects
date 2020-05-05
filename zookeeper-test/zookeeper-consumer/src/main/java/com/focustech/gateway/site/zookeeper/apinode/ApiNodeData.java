package com.focustech.gateway.site.zookeeper.apinode;

import com.focustech.gateway.site.zookeeper.core.NodeData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

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
    //路由类型：fuzzy-模糊匹配，strict-精确匹配
    private String routeType;
    private List<ApiRule> rules;

}
