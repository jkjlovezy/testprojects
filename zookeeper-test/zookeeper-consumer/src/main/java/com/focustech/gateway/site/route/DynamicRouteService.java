package com.focustech.gateway.site.route;

import com.focustech.gateway.site.constant.CommonConstants;
import com.focustech.gateway.site.constant.CommonConstants.*;
import com.focustech.gateway.site.route.data.ApiNodeData;
import com.focustech.gateway.site.util.StringUtils;
import com.focustech.gateway.site.zookeeper.core.NodeEvent;
import com.focustech.gateway.site.zookeeper.core.NodeEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@Order(0)
public class DynamicRouteService implements ApplicationEventPublisherAware, NodeEventListener<ApiNodeData> {

    @Value("${zookeeper.api.path:/gateway/api}")
    private String apiRootPath;

    @Autowired
    private RouteDefinitionWriter routeDefinitionWriter;

    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    @Override
    public void add(NodeEvent<ApiNodeData> nodeEvent) {
        log.debug("DynamicRouteService add node event:{}", nodeEvent);
        routeDefinitionWriter.save(Mono.just(assembleRouteDefinition(nodeEvent))).subscribe();
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    @Override
    public void update(NodeEvent<ApiNodeData> nodeEvent) {
        log.debug("DynamicRouteService update node event:{}", nodeEvent);
        RouteDefinition definition = assembleRouteDefinition(nodeEvent);
        this.routeDefinitionWriter.delete(Mono.just(definition.getId()));
        routeDefinitionWriter.save(Mono.just(definition)).subscribe();
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    @Override
    public void delete(NodeEvent<ApiNodeData> nodeEvent) {
        log.debug("DynamicRouteService delete node event:{}", nodeEvent);
        this.routeDefinitionWriter.delete(Mono.just(nodeEvent.getPath())).subscribe();
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    private RouteDefinition assembleRouteDefinition(NodeEvent<ApiNodeData> nodeEvent) {
        final ApiNodeData api = nodeEvent.getData();
        RouteDefinition definition = new RouteDefinition();
        String apiPath = nodeEvent.getPath().substring(apiRootPath.length());
        definition.setId(apiPath);
        if (StringUtils.isEqual(RouteType.FUZZY.name(), api.getRouteType())) { //路由模糊匹配
            definition.setPredicates(Arrays.asList(new PredicateDefinition("Path=" + apiPath)));

            List<FilterDefinition> filters = new ArrayList<>();
            filters.add(new FilterDefinition("ApiPrecondition"));
            filters.add(new FilterDefinition("CustomRequestRateLimiter"));
            filters.add(new FilterDefinition("StripPrefix=" + (StringUtils.appearNums(apiPath, "/") - 1)));
            if (api.getServicePath() != null && api.getServicePath().length() > 0)
                filters.add(new FilterDefinition("PrefixPath=" + StringUtils.parsePrefixPath(api.getServicePath())));
            definition.setFilters(filters);

            definition.setUri(UriComponentsBuilder.fromUriString(api.getServiceDomain()).build().toUri());
        } else {//路由精确匹配
            definition.setPredicates(Arrays.asList(new PredicateDefinition("Path=" + apiPath)));

            List<FilterDefinition> filters = new ArrayList<>();
            filters.add(new FilterDefinition("ApiPrecondition"));
            filters.add(new FilterDefinition("CustomRequestRateLimiter"));
            definition.setFilters(filters);

            definition.setUri(UriComponentsBuilder.fromUriString(api.getServiceDomain() + api.getServicePath()).build().toUri());
        }

        return definition;
    }


}
