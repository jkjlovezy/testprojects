package com.focustech.gateway.manage.request;

import com.focustech.gateway.manage.entity.ApiEntity;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class ApiPostRequest {

    private Integer serviceId;

    private String apiPath;

    private Integer requestTimeout;//int not null comment '超时时间，单位：毫秒',

    private String httpMethod;// varchar(128) not null default 'POST' comment '请求类型，如POST、GET、DELETE',

    private String passportType;// varchar(32) not null comment '接入方类型：CROV-跨境、INNER-公司内部、PARTNER-合作方',

    private Integer authentStrategy;// int not null comment '认证策略方式： 0-无，1-token、2-验签',

    private Integer status;// tinyint not null default 0 comment '使能开关，0-关闭、1-打开',

    private Integer ruleGroupId;// int not null default '-1' comment '路由规则组ID',

    private Integer flowlimitEnable;// tinyint not null default 0 comment '流控开关，0-关闭、1-打开',

    public ApiEntity convertToEntity(){
        ApiEntity entity = new ApiEntity();
        BeanUtils.copyProperties(this,entity);
        return entity;
    }
}
