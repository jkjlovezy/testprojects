package com.focustech.gateway.site.zookeeper;

import lombok.Data;

@Data
public class ApiInfo {
    private int id;
    private String gatewayPath;
    private String servicePath;
    private Integer timeout;
}
