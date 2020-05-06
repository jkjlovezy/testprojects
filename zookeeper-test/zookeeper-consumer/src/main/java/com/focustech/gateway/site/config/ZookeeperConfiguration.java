package com.focustech.gateway.site.config;

import com.focustech.gateway.site.route.filter.ApiRuleGatewayFilterFactory;
import com.focustech.gateway.site.route.ApiNodeChangeHandler;
import com.focustech.gateway.site.route.ApiNodeChangeListener;
import com.focustech.gateway.site.route.ApiHolder;
import com.focustech.gateway.site.zookeeper.core.NodeChangeListener;
import com.focustech.gateway.site.zookeeper.core.ZookeeperClient;
import com.focustech.gateway.site.zookeeper.servicenode.ServiceNodeData;
import com.focustech.gateway.site.zookeeper.servicenode.ServiceNodeChangeHandler;
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
    public NodeChangeListener apiNodeListener(ApiNodeChangeHandler apiNodeHandler) {
        ApiNodeChangeListener apiNodeListener = new ApiNodeChangeListener(apiNodeHandler, apiRootPath);
        return apiNodeListener;
    }

    @Bean("serviceNodeListener")
    public NodeChangeListener serviceNodeListener(ServiceNodeChangeHandler serviceNodeHandler) {
        NodeChangeListener serviceNodeListener = new NodeChangeListener(serviceNodeHandler, serviceRootPath) {
            @Override
            protected Class getNodeDataClass() {
                return ServiceNodeData.class;
            }
        };
        return serviceNodeListener;
    }

    @Bean
    public ApiRuleGatewayFilterFactory customRuleGatewayFilterFactory(ApiHolder apiRuleCheckProcessor) {
        return new ApiRuleGatewayFilterFactory(apiRuleCheckProcessor);
    }


}
