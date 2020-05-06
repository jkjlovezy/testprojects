package com.focustech.gateway.site.route;

import com.focustech.gateway.site.route.data.ApiNodeData;
import com.focustech.gateway.site.route.data.ApiNodeInfo;
import com.focustech.gateway.site.route.data.ApiRule;
import com.focustech.gateway.site.route.data.ApiRuleCheckResult;
import com.focustech.gateway.site.zookeeper.core.NodeEvent;
import com.focustech.gateway.site.zookeeper.core.NodeEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.focustech.gateway.site.constant.CommonConstants.RuleMatchType;
import static com.focustech.gateway.site.constant.CommonConstants.RuleScope;

@Slf4j
@Component
@Order(1)
public class ApiHolder implements NodeEventListener<ApiNodeData> {
    private Map<String, ApiNodeInfo<ApiNodeData>> apiMap = new ConcurrentHashMap<>();

    @Override
    public void add(NodeEvent<ApiNodeData> nodeEvent) {
        log.debug("ApiHolder add node event:{}", nodeEvent);
        apiMap.put(nodeEvent.getPath(), new ApiNodeInfo<>(nodeEvent.getDataVersion(), nodeEvent.getData()));
    }

    @Override
    public void update(NodeEvent<ApiNodeData> nodeEvent) {
        log.debug("ApiHolder update node event:{}", nodeEvent);
        apiMap.put(nodeEvent.getPath(), new ApiNodeInfo<>(nodeEvent.getDataVersion(), nodeEvent.getData()));
    }

    @Override
    public void delete(NodeEvent<ApiNodeData> nodeEvent) {
        log.debug("ApiHolder delete node event:{}", nodeEvent);
        apiMap.remove(nodeEvent.getPath());
    }

    public Map<String, ApiNodeInfo<ApiNodeData>> getApi() {
        return apiMap;
    }

    public ApiRuleCheckResult checkApiRule(String apiPath, ServerHttpRequest request) {
        ApiRuleCheckResult result = new ApiRuleCheckResult(true);
        if (log.isDebugEnabled())
            log.debug("checkApiRule path={},apiData={}", request.getURI().getPath(), apiMap.get(apiPath));
        List<ApiRule> rules = apiMap.get(apiPath).getData().getRules();
        if (rules != null && rules.size() > 0) {
            for (ApiRule rule : rules) {
                if (isEqual(RuleScope.COOKIE.name(), rule.getRuleScope())) {
                    if (!checkApiRule(rule, request.getCookies(), rule.getParamKey(), HttpCookie::getValue)) {
                        result.setSuccess(false);
                        result.getFailRules().add(rule);
                    }
                } else if (isEqual(RuleScope.REQUEST_PARAM.name(), rule.getRuleScope())) {
                    if (!checkApiRule(rule, request.getQueryParams(), rule.getParamKey(), String::toString)) {
                        result.setSuccess(false);
                        result.getFailRules().add(rule);
                    }
                } else if (isEqual(RuleScope.HEADER.name(), rule.getRuleScope())) {
                    if (!checkApiRule(rule, request.getHeaders(), rule.getParamKey(), String::toString)) {
                        result.setSuccess(false);
                        result.getFailRules().add(rule);
                    }
                }
            }
        }
        return result;
    }

    private <K, V, R> boolean checkApiRule(ApiRule rule, MultiValueMap<K, V> params, K paramKey, Function<V, String> function) {
        if (params == null || params.isEmpty()) {
            return false;
        }
        V paramValue = params.getFirst(paramKey);
        if (isEqual(RuleMatchType.EXIST.name(), rule.getMatchType())) {
            return paramValue != null;
        } else {
            if (paramValue == null) {
                return false;
            }
            if (isEqual(RuleMatchType.EQUAL.name(), rule.getMatchType())) {
                return isEqual(function.apply(paramValue), rule.getParamValue());
            } else if (isEqual(RuleMatchType.INCLUDE.name(), rule.getMatchType())) {
                return isInclude(function.apply(paramValue), rule.getParamValue());
            } else if (isEqual(RuleMatchType.REGEX.name(), rule.getMatchType())) {
                return matchRegex(function.apply(paramValue), rule.getParamValue());
            } else {
                return false;
            }
        }
    }

//    private boolean existRequestParam(ApiRule rule, MultiValueMap<String, String> params) {
//        if (params.isEmpty()) {
//            return false;
//        }
//        String paramValue = params.getFirst(rule.getParamKey());
//        return exist(rule, paramValue, String::toString);
//    }
//
//    private boolean existCookie(ApiRule rule, MultiValueMap<String, HttpCookie> cookies) {
//        if (cookies.isEmpty()) {
//            return false;
//        }
//        HttpCookie httpCookie = cookies.getFirst(rule.getParamKey());
//        return exist(rule, httpCookie, HttpCookie::getValue);
//    }
//
//    private <T> boolean exist(ApiRule rule, T t, Function<T, String> function) {
//        if (isEqual(RuleMatchType.EXIST.name(), rule.getMatchType())) {
//            return t != null;
//        } else {
//            if (t == null) {
//                return false;
//            }
//            if (isEqual(RuleMatchType.EQUAL.name(), rule.getMatchType())) {
//                return isEqual(function.apply(t), rule.getParamValue());
//            } else if (isEqual(RuleMatchType.INCLUDE.name(), rule.getMatchType())) {
//                return isInclude(function.apply(t), rule.getParamValue());
//            } else if (isEqual(RuleMatchType.REGEX.name(), rule.getMatchType())) {
//                return matchRegex(function.apply(t), rule.getParamValue());
//            } else {
//                return false;
//            }
//        }
//
//    }

    private boolean isEqual(String s1, String s2) {
        if (s1 == null || s2 == null) {
            return false;
        }
        return s1.equals(s2);
    }

    private boolean isInclude(String value, String ruleValues) {
        if (ruleValues == null) {
            return false;
        }
        List<String> ruleValueList = Arrays.stream(ruleValues.split(",")).collect(Collectors.toList());
        return ruleValueList.contains(value);
    }

    private boolean matchRegex(String value, String regex) {
        if (value == null) {
            return false;
        }
        return value.matches(regex);
    }


    public static void main(String[] args) {
        ApiHolder holder = new ApiHolder();
        ApiRule rule;

        MultiValueMap<String, HttpCookie> cookies = new LinkedMultiValueMap<>();
        cookies.add("cookie1", new HttpCookie("cookie1", "jkj"));
        rule = new ApiRule();
        rule.setRuleScope("COOKIE");
        rule.setMatchType("EQUAL");
        rule.setParamKey("cookie1");
        rule.setParamValue("jkj");
        System.out.println(holder.checkApiRule(rule, cookies, rule.getParamKey(), HttpCookie::getValue));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("paramName", "jkj");
        rule.setRuleScope("REQUEST_PARAM");
        rule.setMatchType("EQUAL");
        rule.setParamKey("paramName");
        rule.setParamValue("jkj");
        System.out.println(holder.checkApiRule(rule, params, rule.getParamKey(), String::toString));

        HttpHeaders headers = new HttpHeaders();
        headers.add("appKey", "111112");
        rule = new ApiRule();
        rule.setRuleScope("HEADER");
        rule.setMatchType("EQUAL");
        rule.setParamKey("appKey");
        rule.setParamValue("111111");
        System.out.println(holder.checkApiRule(rule, headers, rule.getParamKey(), String::toString));


    }
}
