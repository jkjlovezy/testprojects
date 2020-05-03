package com.focustech.gateway.site.zookeeper.apinode;

import com.focustech.gateway.site.dynamicroute.DynamicRouteService;
import com.focustech.gateway.site.zookeeper.core.AbstractNodeHandler;
import com.focustech.gateway.site.zookeeper.core.NodeEvent;
import com.focustech.gateway.site.zookeeper.core.NodeHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;

@Slf4j
@Component
public class ApiNodeHandler extends AbstractNodeHandler<ApiNodeData> implements NodeHandler {

    @Autowired
    DynamicRouteService dynamicRouteService;

    @Override
    public void doHandle(NodeEvent<ApiNodeData> nodeEvent) {
        log.debug("ApiNodeHandler receive nodeEvent,nodeEvent={}", nodeEvent);
        if (nodeEvent.getData() == null) {
            log.warn("ApiNodeHandler receive nodeEvent but no data; nodeEvent={}", nodeEvent);
            return;
        }
        switch (nodeEvent.getOperation()) {
            case ADDED:
                dynamicRouteService.add(assembleRouteDefinition(nodeEvent));
                break;
            case UPDATED:
                dynamicRouteService.update(assembleRouteDefinition(nodeEvent));
                break;
            case DELETED:
                dynamicRouteService.delete(nodeEvent.getData().getId());
                break;
            default:
        }
    }

    private RouteDefinition assembleRouteDefinition(NodeEvent<ApiNodeData> nodeEvent) {
        final ApiNodeData api = nodeEvent.getData();
        RouteDefinition definition = new RouteDefinition();
        definition.setId(api.getId());
        definition.setPredicates(Arrays.asList(new PredicateDefinition("Path=" + nodeEvent.getPath().replace("**", "{segment}"))));
        definition.setUri(UriComponentsBuilder.fromUriString(api.getServiceDomain()).build().toUri());
        definition.setFilters(Arrays.asList(new FilterDefinition("SetPath=" + api.getServicePath().replace("**", "{segment}"))));
        return definition;
    }
}
