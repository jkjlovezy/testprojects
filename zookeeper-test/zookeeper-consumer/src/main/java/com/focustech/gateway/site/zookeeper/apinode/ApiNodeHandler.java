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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        if ("fuzzy".equalsIgnoreCase(api.getRouteType())) { //路由模糊匹配
            definition.setPredicates(Arrays.asList(new PredicateDefinition("CustomPath=" + nodeEvent.getPath())));
            definition.setUri(UriComponentsBuilder.fromUriString(api.getServiceDomain()).build().toUri());
            List<FilterDefinition> filters = new ArrayList<>();
            filters.add(new FilterDefinition("CustomRule"));
            filters.add(new FilterDefinition("StripPrefix=" + (appearNums(nodeEvent.getPath(), "/") - 1)));
            if (api.getServicePath() != null && api.getServicePath().length() > 0)
                filters.add(new FilterDefinition("PrefixPath=" + parsePrefixPath(api.getServicePath())));
            definition.setFilters(filters);
        } else {//路由精确匹配
            definition.setPredicates(Arrays.asList(new PredicateDefinition("Path=" + nodeEvent.getPath())));
            definition.setUri(UriComponentsBuilder.fromUriString(api.getServiceDomain() + api.getServicePath()).build().toUri());
        }

        return definition;
    }

//    PrefixPath=/test

    /**
     * @param path 举例：/test, /test/**, /test/
     * @return 举例返回/test
     */
    private String parsePrefixPath(String path) {
        int i = path.length() - 1;
        for (; i >= 0; i--) {
            if (path.charAt(i) != '/' && path.charAt(i) != '*') {
                break;
            }
        }
        return path.substring(0, i + 1);
    }

    private int appearNums(String text, String keyword) {
        int count = 0;
        int leng = text.length();
        int j = 0;
        for (int i = 0; i < leng; i++) {
            if (text.charAt(i) == keyword.charAt(j)) {
                j++;
                if (j == keyword.length()) {
                    count++;
                    j = 0;
                }
            } else {
                i = i - j;// should rollback when not match
                j = 0;
            }
        }
        return count;
    }
}
