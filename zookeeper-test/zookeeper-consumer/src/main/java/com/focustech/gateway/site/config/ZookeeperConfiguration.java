package com.focustech.gateway.site.config;

import com.focustech.gateway.site.zookeeper.apinode.ApiNodeHandler;
import com.focustech.gateway.site.zookeeper.apinode.ApiNodeTreeCacheListener;
import com.focustech.gateway.site.zookeeper.core.BaseTreeCacheListener;
import com.focustech.gateway.site.zookeeper.core.ZookeeperClient;
import com.focustech.gateway.site.zookeeper.servicenode.ServiceNodeData;
import com.focustech.gateway.site.zookeeper.servicenode.ServiceNodeHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZookeeperConfiguration {
    @Value("${zookeeper.url}")
    private String zkHostPort;

    @Value("${zookeeper.api.path:/gateway/api}")
    private String apiRootPath;

    @Value("${zookeeper.api.path:/gateway/service}")
    private String serviceRootPath;

    @Bean
    public ZookeeperClient zookeeperClient() {
        ZookeeperClient zookeeperClient = new ZookeeperClient(zkHostPort);
        return zookeeperClient;
    }

    @Bean("apiNodeListener")
    public BaseTreeCacheListener apiNodeListener(ApiNodeHandler apiNodeHandler) {
        ApiNodeTreeCacheListener apiNodeListener = new ApiNodeTreeCacheListener
                (apiNodeHandler, apiRootPath);
        return apiNodeListener;
    }

    @Bean("serviceNodeListener")
    public BaseTreeCacheListener serviceNodeListener(ServiceNodeHandler serviceNodeHandler) {
        BaseTreeCacheListener serviceNodeListener = new BaseTreeCacheListener(serviceNodeHandler, serviceRootPath) {
            @Override
            protected Class getNodeDataClass() {
                return ServiceNodeData.class;
            }
        };
        return serviceNodeListener;
    }

}