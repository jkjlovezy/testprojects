package com.focustech.gateway.site.route.filter;

import com.focustech.gateway.site.route.ApiHolder;
import com.focustech.gateway.site.route.data.ApiNodeData;
import com.focustech.gateway.site.route.data.ApiRateLimit;
import com.focustech.gateway.site.route.data.ApiRuleCheckResult;
import com.focustech.gateway.site.route.filter.ratelimit.CustomRedisRateLimiter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;
import java.util.Map;

public class CustomRequestRateLimiterGatewayFilterFactory extends AbstractGatewayFilterFactory {


    private final CustomRedisRateLimiter customRedisRateLimiter;

    public CustomRequestRateLimiterGatewayFilterFactory(CustomRedisRateLimiter customRedisRateLimiter) {
        this.customRedisRateLimiter = customRedisRateLimiter;
    }


    @SuppressWarnings("unchecked")
    @Override
    public GatewayFilter apply(Object object) {
        return (exchange, chain) -> {
            Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);

            return customRedisRateLimiter.isAllowed(route.getId(), exchange).flatMap(response -> {
//                for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
//                    exchange.getResponse().getHeaders().add(header.getKey(), header.getValue());
//                }

                if (response.isAllowed()) {
                    return chain.filter(exchange);
                }

                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                return exchange.getResponse().setComplete();
            });
//            return resolver.resolve(exchange).flatMap(key ->
//                    // TODO: if key is empty?
//                    limiter.isAllowed(route.getId(), key).flatMap(response -> {
//
//                        for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
//                            exchange.getResponse().getHeaders().add(header.getKey(), header.getValue());
//                        }
//
//                        if (response.isAllowed()) {
//                            return chain.filter(exchange);
//                        }
//
//                        exchange.getResponse().setStatusCode( HttpStatus.TOO_MANY_REQUESTS);
//                        return exchange.getResponse().setComplete();
//                    }));
        };
    }


}

