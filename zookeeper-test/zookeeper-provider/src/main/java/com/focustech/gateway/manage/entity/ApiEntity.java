package com.focustech.gateway.manage.entity;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "gw_api")
@Data
public class ApiEntity extends BaseEntity {

//    @Column(name = "service_id")
//    private Integer serviceId;

    @Column(name = "api_path")
    private String apiPath;

    @Column(name = "request_timeout")
    private Integer requestTimeout;//int not null comment '超时时间，单位：毫秒',

    @Column(name = "http_method")
    private String httpMethod;// varchar(128) not null default 'POST' comment '请求类型，如POST、GET、DELETE',

    @Column(name = "passport_type")
    private String passportType;// varchar(32) not null comment '接入方类型：CROV-跨境、INNER-公司内部、PARTNER-合作方',

    @Column(name = "authent_strategy")
    private Integer authentStrategy;// int not null comment '认证策略方式： 0-无，1-token、2-验签',

    @Column(name = "status")
    private Integer status;// tinyint not null default 0 comment '使能开关，0-关闭、1-打开',

    @Column(name = "rule_group_id")
    private Integer ruleGroupId;// int not null default '-1' comment '路由规则组ID',

    @Column(name = "flowlimit_enable")
    private Integer flowlimitEnable;// tinyint not null default 0 comment '流控开关，0-关闭、1-打开',

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = ServiceEntity.class)
    @JoinColumn(name = "service_id", referencedColumnName = "id")
    private ServiceEntity service;
}
