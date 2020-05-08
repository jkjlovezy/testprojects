package com.focustech.gateway.site.config;

import com.focustech.gateway.site.route.ApiHolder;
import com.focustech.gateway.site.route.ApiNodeChangeHandler;
import com.focustech.gateway.site.route.ApiNodeChangeListener;
import com.focustech.gateway.site.route.filter.ApiPreconditionGatewayFilterFactory;
import com.focustech.gateway.site.route.filter.CustomRequestRateLimiterGatewayFilterFactory;
import com.focustech.gateway.site.route.filter.ratelimit.CustomRedisRateLimiter;
import com.focustech.gateway.site.zookeeper.core.NodeChangeListener;
import com.focustech.gateway.site.zookeeper.core.ZookeeperClient;
import com.focustech.gateway.site.zookeeper.servicenode.ServiceNodeChangeHandler;
import com.focustech.gateway.site.zookeeper.servicenode.ServiceNodeData;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.List;

@Configuration
public class ZookeeperConfiguration {
    @Value("${zookeeper.url}")
    private String zkHostPort;

    @Value("${zookeeper.api.path:/gateway/api}")
    private String apiRootPath;

    @Value("${zookeeper.api.path:/gateway/service}")
    private String serviceRootPath;

    @Value("${rate.limit.script.path:scripts/custom_rate_limit.lua}")
    private String customRedisRateLimterScriptPath;

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
    @Qualifier(CustomRedisRateLimiter.CUSTOM_REDIS_SCRIPT_NAME)
    public DefaultRedisScript customRedisRateLimiterScript() {
        DefaultRedisScript redisScript = new DefaultRedisScript<List<Long>>();
        redisScript.setResultType(List.class);
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(customRedisRateLimterScriptPath)));
        return redisScript;
    }

    @Bean
    @Primary
    public CustomRedisRateLimiter customRedisRateLimiter(StringRedisTemplate redisTemplate,
                                                         @Qualifier(CustomRedisRateLimiter.CUSTOM_REDIS_SCRIPT_NAME) RedisScript<List<Long>> redisScript, ApiHolder apiHolder) {
        return new CustomRedisRateLimiter(redisTemplate, redisScript, apiHolder);
    }

    @Bean
    public CustomRequestRateLimiterGatewayFilterFactory customRequestRateLimiterGatewayFilterFactory(CustomRedisRateLimiter customRedisRateLimiter) {
        return new CustomRequestRateLimiterGatewayFilterFactory(customRedisRateLimiter);
    }

}
