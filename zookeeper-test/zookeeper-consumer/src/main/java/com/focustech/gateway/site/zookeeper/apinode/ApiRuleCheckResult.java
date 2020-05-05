package com.focustech.gateway.site.zookeeper.apinode;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class ApiRuleCheckResult {
    private boolean success;
    private List<ApiRule> failRules = new ArrayList<ApiRule>();

    public ApiRuleCheckResult() {

    }

    public ApiRuleCheckResult(boolean success) {
        this.success = success;
    }
}
