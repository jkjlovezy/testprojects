package com.focustech.gateway.site.dynamicroute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DynamicRouteService implements ApplicationEventPublisherAware {
    @Autowired
    private RouteDefinitionWriter routeDefinitionWriter;
    private ApplicationEventPublisher publisher;

    private  void notifyChanged(){
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    public void add(RouteDefinition definition){
        routeDefinitionWriter.save(Mono.just(definition)).subscribe();
        notifyChanged();
    }

    public void update(RouteDefinition definition){
        this.routeDefinitionWriter.delete(Mono.just(definition.getId()));
        routeDefinitionWriter.save(Mono.just(definition)).subscribe();
        notifyChanged();
    }

    public void delete(String id){
        this.routeDefinitionWriter.delete(Mono.just(id)).subscribe();
        notifyChanged();
    }
}
