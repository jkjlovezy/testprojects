package com.focustech.gateway.site.zookeeper.apinode;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ApiRule {
    //规则范围，枚举值：HEADER-头部、REQUEST_PARAM-查询参数、COOKIE-cookie
    private String ruleScope;
    //匹配类型，枚举值：EQUAL-相等、INCLUDE-包含、REGULAR-正则匹配、EXIST-存在即可
    private String matchType;
    //参数名称
    private String paramKey;
    //参数值
    private String paramValue;

    public ApiRule(){

    }

    public ApiRule(String ruleScope, String matchType, String paramKey, String paramValue) {
        this.ruleScope = ruleScope;
        this.matchType = matchType;
        this.paramKey = paramKey;
        this.paramValue = paramValue;
    }
}
