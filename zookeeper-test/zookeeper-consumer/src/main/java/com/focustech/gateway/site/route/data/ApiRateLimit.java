package com.focustech.gateway.site.route.data;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ApiRateLimit {
    //令牌桶填充速率
    private int replenishRate;
    //令牌桶的容量
    private int burstCapacity;
    //流控粒度，枚举值：SECOND-秒、MINUTE-分钟、HOUR-小时、DAY-天
    private String timeUnit;
    //流控维度，枚举值:API、CLIENT_IP、HOST
    private String type;

    public ApiRateLimit(){

    }

    public ApiRateLimit(int replenishRate, int burstCapacity, String timeUnit, String type) {
        this.replenishRate = replenishRate;
        this.burstCapacity = burstCapacity;
        this.timeUnit = timeUnit;
        this.type = type;
    }
}
