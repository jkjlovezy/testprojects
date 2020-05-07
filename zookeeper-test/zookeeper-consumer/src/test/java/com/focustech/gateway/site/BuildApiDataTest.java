package com.focustech.gateway.site;

import com.alibaba.fastjson.JSON;
import com.focustech.gateway.site.route.data.ApiNodeData;
import com.focustech.gateway.site.route.data.ApiRateLimit;
import com.focustech.gateway.site.route.data.ApiRule;

import java.util.ArrayList;

public class BuildApiDataTest {
    public static void main(String[] args) {
        ApiNodeData data = new ApiNodeData();
        data.setServiceDomain("http://localhost:8000");
        data.setServicePath("/test/testRateLimit");
        data.setRouteType("strict");
        data.setRules(new ArrayList<ApiRule>());
        data.getRules().add(new ApiRule("HEADER","EQUAL","APP_KEY","11111"));
        data.getRules().add(new ApiRule("REQUEST_PARAM","EQUAL","APP_NAME","apptest"));
        data.setRateLimits(new ArrayList<ApiRateLimit>());
        data.getRateLimits().add(new ApiRateLimit(1,"HOUR","API") );
        System.out.println(JSON.toJSONString(data));
    }
}
