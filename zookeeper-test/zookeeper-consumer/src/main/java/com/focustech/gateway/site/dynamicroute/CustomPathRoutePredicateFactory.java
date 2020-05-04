package com.focustech.gateway.site.dynamicroute;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.RoutePredicateFactory;
import org.springframework.core.style.ToStringCreator;
import org.springframework.http.server.PathContainer;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.URI_TEMPLATE_VARIABLES_ATTRIBUTE;
import static org.springframework.http.server.PathContainer.parsePath;


public class CustomPathRoutePredicateFactory extends AbstractRoutePredicateFactory<CustomPathRoutePredicateFactory.Config> {
    private static final Log log = LogFactory.getLog(RoutePredicateFactory.class);

    private PathPatternParser pathPatternParser = new PathPatternParser();

    public CustomPathRoutePredicateFactory() {
        super(CustomPathRoutePredicateFactory.Config.class);
    }

    public void setPathPatternParser(PathPatternParser pathPatternParser) {
        this.pathPatternParser = pathPatternParser;
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList(PATTERN_KEY);
    }

    @Override
    public Predicate<ServerWebExchange> apply(CustomPathRoutePredicateFactory.Config config) {
        synchronized (this.pathPatternParser) {
            config.pathPattern = this.pathPatternParser.parse(config.pattern);
        }
        return exchange -> {
            PathContainer path = parsePath(exchange.getRequest().getURI().getPath());

            boolean match = config.pathPattern.matches(path);
            traceMatch("Pattern", config.pathPattern.getPatternString(), path, match);
            if (match) {
                PathPattern.PathMatchInfo uriTemplateVariables = config.pathPattern.matchAndExtract(path);
                exchange.getAttributes().put(URI_TEMPLATE_VARIABLES_ATTRIBUTE, uriTemplateVariables);
                exchange.getAttributes().put("route_path_pattern", config.pattern);
                return true;
            } else {
                return false;
            }
        };
    }

    private static void traceMatch(String prefix, Object desired, Object actual, boolean match) {
        if (log.isTraceEnabled()) {
            String message = String.format("%s \"%s\" %s against value \"%s\"",
                    prefix, desired, match ? "matches" : "does not match", actual);
            log.trace(message);
        }
    }

    @Validated
    public static class Config {
        private String pattern;
        private PathPattern pathPattern;

        public String getPattern() {
            return pattern;
        }

        public CustomPathRoutePredicateFactory.Config setPattern(String pattern) {
            this.pattern = pattern;
            return this;
        }

        @Override
        public String toString() {
            return new ToStringCreator(this)
                    .append("pattern", pattern)
                    .toString();
        }
    }


}
