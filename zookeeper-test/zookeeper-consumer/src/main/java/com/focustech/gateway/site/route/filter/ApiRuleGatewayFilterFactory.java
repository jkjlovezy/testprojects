package com.focustech.gateway.site.route.filter;

import com.focustech.gateway.site.route.ApiHolder;
import com.focustech.gateway.site.route.data.ApiRuleCheckResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Flux;

import java.nio.charset.Charset;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

@Slf4j
public class ApiRuleGatewayFilterFactory extends AbstractGatewayFilterFactory {
    private ApiHolder apiHolder;

    public ApiRuleGatewayFilterFactory(ApiHolder apiHolder) {
        this.apiHolder = apiHolder;
    }

    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            Route route = (Route) (exchange.getAttribute(GATEWAY_ROUTE_ATTR));
            ApiRuleCheckResult result = apiHolder.checkApiRule(route.getId(), exchange.getRequest());
            if (!result.isSuccess()) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.PRECONDITION_FAILED);
                StringBuilder sb = new StringBuilder("缺失参数:");
                result.getFailRules().stream().forEach(apiRule ->
                        sb.append(String.format("%s(请求域:%s)", apiRule.getParamKey(), apiRule.getRuleScope())).append(",")
                );
                return response.writeWith(Flux.just(response.bufferFactory().wrap(sb.substring(0, sb.length() - 1).getBytes(Charset.forName("UTF-8")))));
            }
            return chain.filter(exchange);
        };
    }
//	@Override
//	public GatewayFilter apply(Config config) {
//		return (exchange, chain) ->  {
//			ServerHttpRequest request = exchange.getRequest();
//			addOriginalRequestUrl(exchange, request.getURI());
//			String path = request.getURI().getRawPath();
//			String newPath = "/" + Arrays.stream(StringUtils.tokenizeToStringArray(path, "/"))
//					.skip(config.parts).collect(Collectors.joining("/"));
//			ServerHttpRequest newRequest = request.mutate()
//					.path(newPath)
//					.build();
//
//			exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, newRequest.getURI());
//
//			return chain.filter(exchange.mutate().request(newRequest).build());
//		};
//	}


}
