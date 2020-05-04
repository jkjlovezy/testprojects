package com.focustech.gateway.site.dynamicroute;

import com.focustech.gateway.site.zookeeper.apinode.ApiRuleCheckProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;

@Slf4j
public class CustomRuleGatewayFilterFactory extends AbstractGatewayFilterFactory {
    private ApiRuleCheckProcessor apiRuleCheckProcessor;

    public CustomRuleGatewayFilterFactory(ApiRuleCheckProcessor apiRuleCheckProcessor) {
        this.apiRuleCheckProcessor = apiRuleCheckProcessor;
    }

    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            log.info("-------"+exchange.getAttribute("route_path_pattern"));
            apiRuleCheckProcessor.checkApiRule(exchange.getRequest());
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
