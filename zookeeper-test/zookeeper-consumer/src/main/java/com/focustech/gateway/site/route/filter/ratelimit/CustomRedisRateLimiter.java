package com.focustech.gateway.site.route.filter.ratelimit;

import com.focustech.gateway.site.route.ApiHolder;
import com.focustech.gateway.site.route.data.ApiNodeData;
import com.focustech.gateway.site.route.data.ApiRateLimit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.cloud.gateway.filter.ratelimit.AbstractRateLimiter;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class CustomRedisRateLimiter extends AbstractRateLimiter<CustomRedisRateLimiter.Config> implements ApplicationContextAware {
    @Deprecated
    public static final String REPLENISH_RATE_KEY = "replenishRate";
    @Deprecated
    public static final String BURST_CAPACITY_KEY = "burstCapacity";

    public static final String CONFIGURATION_PROPERTY_NAME = "redis-rate-limiter";
    public static final String REDIS_SCRIPT_NAME = "redisRequestRateLimiterScript";
    public static final String REMAINING_HEADER = "X-RateLimit-Remaining";
    public static final String REPLENISH_RATE_HEADER = "X-RateLimit-Replenish-Rate";
    public static final String BURST_CAPACITY_HEADER = "X-RateLimit-Burst-Capacity";
    public static final String API_HOLDER_NAME = "apiHolder";

    private Log log = LogFactory.getLog(getClass());

    private ReactiveRedisTemplate<String, String> redisTemplate;
    private RedisScript<List<Long>> script;
    private AtomicBoolean initialized = new AtomicBoolean(false);
    private Config defaultConfig;
    private ApiHolder apiHolder;

    // configuration properties
    /**
     * Whether or not to include headers containing rate limiter information, defaults to true.
     */
    private boolean includeHeaders = true;

    /**
     * The name of the header that returns number of remaining requests during the current second.
     */
    private String remainingHeader = REMAINING_HEADER;

    /**
     * The name of the header that returns the replenish rate configuration.
     */
    private String replenishRateHeader = REPLENISH_RATE_HEADER;

    /**
     * The name of the header that returns the burst capacity configuration.
     */
    private String burstCapacityHeader = BURST_CAPACITY_HEADER;

    public CustomRedisRateLimiter(ReactiveRedisTemplate<String, String> redisTemplate,
                                  RedisScript<List<Long>> script, Validator validator, ApiHolder apiHolder) {
        super(Config.class, CONFIGURATION_PROPERTY_NAME, validator);
        this.redisTemplate = redisTemplate;
        this.script = script;
        this.apiHolder = apiHolder;
        initialized.compareAndSet(false, true);
    }

    public CustomRedisRateLimiter(int defaultReplenishRate, int defaultBurstCapacity) {
        super(Config.class, CONFIGURATION_PROPERTY_NAME, null);
        this.defaultConfig = new Config()
                .setReplenishRate(defaultReplenishRate)
                .setBurstCapacity(defaultBurstCapacity);
    }

    public boolean isIncludeHeaders() {
        return includeHeaders;
    }

    public void setIncludeHeaders(boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
    }

    public String getRemainingHeader() {
        return remainingHeader;
    }

    public void setRemainingHeader(String remainingHeader) {
        this.remainingHeader = remainingHeader;
    }

    public String getReplenishRateHeader() {
        return replenishRateHeader;
    }

    public void setReplenishRateHeader(String replenishRateHeader) {
        this.replenishRateHeader = replenishRateHeader;
    }

    public String getBurstCapacityHeader() {
        return burstCapacityHeader;
    }

    public void setBurstCapacityHeader(String burstCapacityHeader) {
        this.burstCapacityHeader = burstCapacityHeader;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        if (initialized.compareAndSet(false, true)) {
            this.redisTemplate = context.getBean("stringReactiveRedisTemplate", ReactiveRedisTemplate.class);
            this.script = context.getBean(REDIS_SCRIPT_NAME, RedisScript.class);
            this.apiHolder = context.getBean(API_HOLDER_NAME, ApiHolder.class);
            if (context.getBeanNamesForType(Validator.class).length > 0) {
                this.setValidator(context.getBean(Validator.class));
            }
        }
    }

    /* for testing */ Config getDefaultConfig() {
        return defaultConfig;
    }

    public Mono<Response> isAllowed(String routeId, ServerWebExchange exchange) {
        if (!this.initialized.get()) {
            throw new IllegalStateException("RedisRateLimiter is not initialized");
        }

//        Config routeConfig = getConfig().getOrDefault(routeId, defaultConfig);
//
//        if (routeConfig == null) {
//            throw new IllegalArgumentException("No Configuration found for route " + routeId);
//        }

        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);

        ApiNodeData api = apiHolder.getApi().get(route.getId()).getData();
        List<ApiRateLimit> rateLimits = api.getRateLimits();
        if (rateLimits == null || rateLimits.size() == 0)
            return Mono.just(new Response(true, new HashMap<String, String>()));
//        Mono<Response> response = Flux.fromIterable(rateLimits).map(limit -> isAllowed(route.getId(), getKeyId(routeId, limit, exchange), limit.getQpsLimit() + "", getEpochTime(limit.getQpsStep())))
//                .takeUntil(s -> !s.block().isAllowed()).blockLast();
//        return response;
        AtomicBoolean allowed = new AtomicBoolean(true);
//        Flux.fromIterable(rateLimits).map(limit -> isAllowed(route.getId(), getKeyId(routeId, limit, exchange), limit.getQpsLimit() + "", getEpochTime(limit.getQpsStep())))
//                .takeUntil(s -> !s.block().isAllowed()).subscribe(s->{
//                    if(s.block().isAllowed()){
//                        allowed.set(false);
//                    }
//                });
        for (ApiRateLimit limit : rateLimits) {
            Mono<Response> response = isAllowed(getKeyId(routeId, limit, exchange), limit.getReplenishRate(), limit.getBurstCapacity(), getEpochTime(limit.getTimeUnit()));
            response.subscribe(r -> {
                if (!r.isAllowed()) {
                    allowed.set(false);
                }
            });
            //不启作用，上面设置false是异步操作。
            if (!allowed.get()) {
                break;
            }
        }
        return Mono.just(new Response(allowed.get(), new HashMap<String, String>()));
    }


    public Mono<Response> isAllowed(String id, int replenishRate, int burstCapacity, long epochTime) {
        try {
            log.debug("----enter isAllowed");
            List<String> keys = getKeys(id);
            // The arguments to the LUA script. time() returns unixtime in seconds.
            List<String> scriptArgs = Arrays.asList(replenishRate + "", burstCapacity + "",
                    epochTime + "", "1");
            // allowed, tokens_left = redis.eval(SCRIPT, keys, args)
            Flux<List<Long>> flux = this.redisTemplate.execute(this.script, keys, scriptArgs);
            // .log("redisratelimiter", Level.FINER);
            return flux.onErrorResume(throwable -> Flux.just(Arrays.asList(1L, -1L)))
                    .reduce(new ArrayList<Long>(), (longs, l) -> {
                        longs.addAll(l);
                        return longs;
                    }).map(results -> {
                        boolean allowed = results.get(0) == 1L;
                        Long tokensLeft = results.get(1);

                        RateLimiter.Response response = new RateLimiter.Response(allowed, new HashMap<String, String>());

                        if (log.isDebugEnabled()) {
                            log.debug("tokensLeft: " + tokensLeft);
                            log.debug("response: " + response);
                        }
                        return response;
                    });
        } catch (Exception e) {
            /*
             * We don't want a hard dependency on Redis to allow traffic. Make sure to set
             * an alert so you know if this is happening too much. Stripe's observed
             * failure rate is 0.01%.
             */
            log.error("Error determining if user allowed from redis", e);
        }
        return Mono.just(new RateLimiter.Response(true, new HashMap<String, String>()));
    }

    private String getKeyId(String routeId, ApiRateLimit limit, ServerWebExchange exchange) {
        if ("API".equals(limit.getType())) {
            return routeId;
        } else if ("CLIENT_IP".equals(limit.getType())) {
            return routeId + exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        } else if ("HOST".equals(limit.getType())) {
            return routeId + exchange.getRequest().getHeaders().getFirst("Host");
        } else {
            return routeId;
        }
    }

    //    SECOND-秒、MINUTE-分钟、HOURE-小时、DAY-天
    private long getEpochTime(String qpsStep) {
        switch (qpsStep) {
            case "SECOND":
                return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
            case "MINUTE":
                return TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis());
            case "HOUR":
                return TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis());
            case "DAY":
                return TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis());
            default:
                throw new IllegalArgumentException("unsupported qpsStep: " + qpsStep);
        }
    }

    /**
     * This uses a basic token bucket algorithm and relies on the fact that Redis scripts
     * execute atomically. No other operations can run between fetching the count and
     * writing the new count.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Mono<Response> isAllowed(String routeId, String id) {
        return Mono.just(new Response(true, new HashMap<String, String>()));
    }
//    public Mono<Response> isAllowed(String routeId, String id) {
//        if (!this.initialized.get()) {
//            throw new IllegalStateException("RedisRateLimiter is not initialized");
//        }
//
//        Config routeConfig = getConfig().getOrDefault(routeId, defaultConfig);
//
//        if (routeConfig == null) {
//            throw new IllegalArgumentException("No Configuration found for route " + routeId);
//        }
//
//        // How many requests per second do you want a user to be allowed to do?
//        int replenishRate = routeConfig.getReplenishRate();
//
//        // How much bursting do you want to allow?
//        int burstCapacity = routeConfig.getBurstCapacity();
//
//        try {
//            List<String> keys = getKeys(id);
//
//
//            // The arguments to the LUA script. time() returns unixtime in seconds.
//            List<String> scriptArgs = Arrays.asList(replenishRate + "", burstCapacity + "",
//                    Instant.now().getEpochSecond() + "", "1");
//            // allowed, tokens_left = redis.eval(SCRIPT, keys, args)
//            Flux<List<Long>> flux = this.redisTemplate.execute(this.script, keys, scriptArgs);
//            // .log("redisratelimiter", Level.FINER);
//            return flux.onErrorResume(throwable -> Flux.just(Arrays.asList(1L, -1L)))
//                    .reduce(new ArrayList<Long>(), (longs, l) -> {
//                        longs.addAll(l);
//                        return longs;
//                    }) .map(results -> {
//                        boolean allowed = results.get(0) == 1L;
//                        Long tokensLeft = results.get(1);
//
//                        RateLimiter.Response response = new RateLimiter.Response(allowed, getHeaders(routeConfig, tokensLeft));
//
//                        if (log.isDebugEnabled()) {
//                            log.debug("response: " + response);
//                        }
//                        return response;
//                    });
//        }
//        catch (Exception e) {
//            /*
//             * We don't want a hard dependency on Redis to allow traffic. Make sure to set
//             * an alert so you know if this is happening too much. Stripe's observed
//             * failure rate is 0.01%.
//             */
//            log.error("Error determining if user allowed from redis", e);
//        }
//        return Mono.just(new RateLimiter.Response(true, getHeaders(routeConfig, -1L)));
//    }

    @NotNull
    public HashMap<String, String> getHeaders(Config config, Long tokensLeft) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(this.remainingHeader, tokensLeft.toString());
        headers.put(this.replenishRateHeader, String.valueOf(config.getReplenishRate()));
        headers.put(this.burstCapacityHeader, String.valueOf(config.getBurstCapacity()));
        return headers;
    }

    static List<String> getKeys(String id) {
        // use `{}` around keys to use Redis Key hash tags
        // this allows for using redis cluster

        // Make a unique key per user.
        String prefix = "request_rate_limiter.{" + id;

        // You need two Redis keys for Token Bucket.
        String tokenKey = prefix + "}.tokens";
        String timestampKey = prefix + "}.timestamp";
        return Arrays.asList(tokenKey, timestampKey);
    }

    @Validated
    public static class Config {
        @Min(1)
        private int replenishRate;

        @Min(1)
        private int burstCapacity = 1;

        public int getReplenishRate() {
            return replenishRate;
        }

        public Config setReplenishRate(int replenishRate) {
            this.replenishRate = replenishRate;
            return this;
        }

        public int getBurstCapacity() {
            return burstCapacity;
        }

        public Config setBurstCapacity(int burstCapacity) {
            this.burstCapacity = burstCapacity;
            return this;
        }

        @Override
        public String toString() {
            return "Config{" +
                    "replenishRate=" + replenishRate +
                    ", burstCapacity=" + burstCapacity +
                    '}';
        }
    }
}
