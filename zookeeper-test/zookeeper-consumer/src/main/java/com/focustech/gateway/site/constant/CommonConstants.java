package com.focustech.gateway.site.constant;

public class CommonConstants {
    public static enum RuleMatchType {
        EQUAL, INCLUDE, REGEX, EXIST
    }

    public static  enum RuleScope {
        HEADER, REQUEST_PARAM, COOKIE
    }
}
