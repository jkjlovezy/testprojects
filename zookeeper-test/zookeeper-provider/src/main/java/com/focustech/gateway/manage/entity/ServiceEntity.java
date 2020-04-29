package com.focustech.gateway.manage.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "gw_service")
@Data
public class ServiceEntity extends BaseEntity {
    @Column(name = "application_id")
    private Integer applicationId;//所属应用ID，对应gw_application.id

    @Column(name = "module_id")
    private Integer moduleId;//所属模块ID，对应gw_module.id

    @Column(name = "service_name")
    private String serviceName;//接口名称，如：商品详情查询接口

    @Column(name = "service_path")
    private String servicePath;// 接口路径，如：/goods/goodsDetail

    @Column(name = "http_method")
    private String httpMethod;// 请求类型，如POST、GET、DELETE

    @Column(name = "api_desc")
    private String apiDesc;//描述


}
