package com.focustech.gateway.site.route.filter.ratelimit;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.focustech.gateway.site.route.ApiHolder;
import com.focustech.gateway.site.route.data.ApiNodeData;
import com.focustech.gateway.site.route.data.ApiRateLimit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.cloud.gateway.filter.ratelimit.AbstractRateLimiter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.validation.Validator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class CustomRedisRateLimiter extends AbstractRateLimiter implements ApplicationContextAware {
    public static final String CONFIGURATION_PROPERTY_NAME = "redis-rate-limiter";
    public static final String CUSTOM_REDIS_SCRIPT_NAME = "customRedisRateLimiterScript";
    public static final String API_HOLDER_NAME = "apiHolder";

    private StringRedisTemplate redisTemplate;
    private RedisScript<List<Long>> script;
    private ApiHolder apiHolder;
    private AtomicBoolean initialized = new AtomicBoolean(false);


    public CustomRedisRateLimiter(StringRedisTemplate redisTemplate, RedisScript<List<Long>> script, ApiHolder apiHolder) {
        super(Object.class, CONFIGURATION_PROPERTY_NAME, null);
        this.redisTemplate = redisTemplate;
        this.script = script;
        this.apiHolder = apiHolder;
        initialized.compareAndSet(false, true);
    }


    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        if (initialized.compareAndSet(false, true)) {
            this.redisTemplate = context.getBean("stringRedisTemplate", StringRedisTemplate.class);
            this.script = context.getBean(CUSTOM_REDIS_SCRIPT_NAME, RedisScript.class);
            this.apiHolder = context.getBean(API_HOLDER_NAME, ApiHolder.class);
            if (context.getBeanNamesForType(Validator.class).length > 0) {
                this.setValidator(context.getBean(Validator.class));
            }
        }
    }


    public Mono<Response> isAllowed(String routeId, ServerWebExchange exchange) {
        if (!this.initialized.get()) {
            throw new IllegalStateException("RedisRateLimiter is not initialized");
        }
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        ApiNodeData api = apiHolder.getApi().get(route.getId()).getData();
        List<ApiRateLimit> rateLimits = api.getRateLimits();
        if (rateLimits == null || rateLimits.size() == 0)
            return Mono.just(new Response(true, new HashMap<String, String>()));
        for (ApiRateLimit limit : rateLimits) {
            List<String> timeStep = getEpochTime(limit.getTimeUnit());
            Response response = isAllowed(getKeyId(routeId, limit, exchange), limit.getReplenishRate(), limit.getReplenishRate(), timeStep.get(0),timeStep.get(1));
            if (!response.isAllowed()) {
                return Mono.just(response);
            }
        }
        return Mono.just(new Response(true, new HashMap<String, String>()));
    }


    public Response isAllowed(String id, int replenishRate, int burstCapacity, String epochTime, String ttlTimeUnit) {
        try {
            log.debug("isAllowed method params:id={},replenishRate={},burstCapacity={},epochTime={},ttlTimeUnit={}",id,replenishRate,burstCapacity,epochTime,ttlTimeUnit);
            List<String> scriptKeys = getKeys(id);
            String[] scriptArgs = new String[]{String.valueOf(replenishRate), String.valueOf(burstCapacity), String.valueOf(epochTime), "1",String.valueOf(ttlTimeUnit)};
            JavaType resultSerializerType = TypeFactory.defaultInstance().constructCollectionType(List.class, Long.class);
            List<Long> results = this.redisTemplate.execute(this.script, new StringRedisSerializer(), new Jackson2JsonRedisSerializer(resultSerializerType), scriptKeys, scriptArgs);
            if (log.isDebugEnabled())
                log.debug("lua script results:" + JSON.toJSONString(results));
            boolean allowed = results.get(0) == 1L;
//            Long tokensLeft = results.get(1);
            return new Response(allowed, new HashMap<String, String>());

        } catch (Exception e) {
            /*
             * We don't want a hard dependency on Redis to allow traffic. Make sure to set
             * an alert so you know if this is happening too much. Stripe's observed
             * failure rate is 0.01%.
             */
            log.error("Error determining if user allowed from redis", e);
        }
        return new Response(true, new HashMap<String, String>());
    }

    private String getKeyId(String routeId, ApiRateLimit limit, ServerWebExchange exchange) {
        String key = routeId+limit.getTimeUnit();
        switch (limit.getType()) {
            case "API":
                return key;
            case "CLIENT_IP":
                return key + exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            case "HOST":
                return key + exchange.getRequest().getHeaders().getFirst("Host");
            default:
                throw new IllegalArgumentException("unsupported rateLimitType: " + limit.getType());
        }
    }

    //    SECOND-秒、MINUTE-分钟、HOURE-小时、DAY-天
    private List<String> getEpochTime(String timeUnit) {
        switch (timeUnit) {
            case "SECOND":
                return Arrays.asList(String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())),"1");
            case "MINUTE":
                return Arrays.asList(String.valueOf(TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis())),"60");
            case "HOUR":
                return Arrays.asList(String.valueOf(TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis())),String.valueOf(60*60));
            case "DAY":
                return Arrays.asList(String.valueOf(TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis())),String.valueOf(60*60*24));
            default:
                throw new IllegalArgumentException("unsupported timeUnit: " + timeUnit);
        }
    }

    private static List<String> getKeys(String id) {
        // use `{}` around keys to use Redis Key hash tags this allows for using redis cluster Make a unique key per user.
        String prefix = "request_rate_limiter.{" + id;
        // You need two Redis keys for Token Bucket.
        String tokenKey = prefix + "}.tokens";
        String timestampKey = prefix + "}.timestamp";
        return Arrays.asList(tokenKey, timestampKey);
    }

    /**
     * This uses a basic token bucket algorithm and relies on the fact that Redis scripts
     * execute atomically. No other operations can run between fetching the count and
     * writing the new count.
     */
    @Override
    @Deprecated
    public Mono<Response> isAllowed(String routeId, String id) {
        return Mono.just(new Response(true, new HashMap<String, String>()));
    }


}

