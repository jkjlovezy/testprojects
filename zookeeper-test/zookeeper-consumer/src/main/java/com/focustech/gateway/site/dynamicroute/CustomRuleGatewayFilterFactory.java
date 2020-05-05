package com.focustech.gateway.site.dynamicroute;

import com.focustech.gateway.site.zookeeper.apinode.ApiHolder;
import com.focustech.gateway.site.zookeeper.apinode.ApiRuleCheckResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;

@Slf4j
public class CustomRuleGatewayFilterFactory extends AbstractGatewayFilterFactory {
    private ApiHolder apiHolder;

    public CustomRuleGatewayFilterFactory(ApiHolder apiHolder) {
        this.apiHolder = apiHolder;
    }

    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            log.info("-------" + exchange.getAttribute("route_path_pattern"));
            ApiRuleCheckResult result = apiHolder.checkApiRule((String) exchange.getAttribute("route_path_pattern"), exchange.getRequest());
            if (!result.isSuccess()) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.PRECONDITION_FAILED);
                return response.setComplete();
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
