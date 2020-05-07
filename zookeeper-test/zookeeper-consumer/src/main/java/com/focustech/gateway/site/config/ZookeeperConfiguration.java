package com.focustech.gateway.site.config;

import com.focustech.gateway.site.route.filter.ApiPreconditionGatewayFilterFactory;
import com.focustech.gateway.site.route.ApiNodeChangeHandler;
import com.focustech.gateway.site.route.ApiNodeChangeListener;
import com.focustech.gateway.site.route.ApiHolder;
import com.focustech.gateway.site.route.filter.CustomRequestRateLimiterGatewayFilterFactory;
import com.focustech.gateway.site.route.filter.ratelimit.CustomRedisRateLimiter;
import com.focustech.gateway.site.zookeeper.core.NodeChangeListener;
import com.focustech.gateway.site.zookeeper.core.ZookeeperClient;
import com.focustech.gateway.site.zookeeper.servicenode.ServiceNodeData;
import com.focustech.gateway.site.zookeeper.servicenode.ServiceNodeChangeHandler;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.validation.Validator;

import java.util.List;

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
    public TreeCacheListener apiNodeListener(ApiNodeChangeHandler apiNodeHandler) {
        TreeCacheListener apiNodeListener = new ApiNodeChangeListener(apiNodeHandler, apiRootPath);
        return apiNodeListener;
    }

    @Bean("serviceNodeListener")
    public TreeCacheListener serviceNodeListener(ServiceNodeChangeHandler serviceNodeHandler) {
        TreeCacheListener serviceNodeListener = new NodeChangeListener(serviceNodeHandler, serviceRootPath) {
            @Override
            protected Class getNodeDataClass() {
                return ServiceNodeData.class;
            }
        };
        return serviceNodeListener;
    }

    @Bean
    public ApiPreconditionGatewayFilterFactory customRuleGatewayFilterFactory(ApiHolder apiRuleCheckProcessor) {
        return new ApiPreconditionGatewayFilterFactory(apiRuleCheckProcessor);
    }

    @Bean
    @Primary
    public CustomRedisRateLimiter customRedisRateLimiter(ReactiveRedisTemplate<String, String> redisTemplate,
                                                         @Qualifier(RedisRateLimiter.REDIS_SCRIPT_NAME) RedisScript<List<Long>> redisScript, ApiHolder apiHolder) {
        return new CustomRedisRateLimiter(redisTemplate, redisScript, null, apiHolder);
    }

    @Bean
    public CustomRequestRateLimiterGatewayFilterFactory customRequestRateLimiterGatewayFilterFactory(CustomRedisRateLimiter customRedisRateLimiter) {
        return new CustomRequestRateLimiterGatewayFilterFactory(customRedisRateLimiter);
    }

}
