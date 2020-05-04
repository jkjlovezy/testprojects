package com.focustech.gateway.site.zookeeper.apinode;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApiRuleCheckProcessor {
    public boolean checkApiRule(ServerHttpRequest request){
        log.debug("checkApiRule path={},rowPath={}",request.getURI().getPath(),request.getURI().getRawPath());
        return true;
    }
}
