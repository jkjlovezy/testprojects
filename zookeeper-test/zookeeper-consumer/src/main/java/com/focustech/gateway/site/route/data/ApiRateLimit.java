package com.focustech.gateway.site.route.data;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ApiRateLimit {
    //阈值
    private int qpsLimit;
    //流控粒度，枚举值：SECOND-秒、MINUTE-分钟、HOUR-小时、DAY-天
    private String qpsStep;
    //流控维度，枚举值:API、CLIENT_IP、HOST
    private String type;

    public ApiRateLimit(){

    }

    public ApiRateLimit(int qpsLimit, String qpsStep, String type) {
        this.qpsLimit = qpsLimit;
        this.qpsStep = qpsStep;
        this.type = type;
    }
}
