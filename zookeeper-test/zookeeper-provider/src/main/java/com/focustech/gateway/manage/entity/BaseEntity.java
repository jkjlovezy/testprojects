package com.focustech.gateway.manage.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "adder_no")
    private String adderNo = "999999";//varchar(50) not null comment '添加人编号',

    @Column(name = "adder_name")
    private String adderName = "SYSTEM";//varchar(50) not null comment '添加人姓名',

    @Column(name = "add_time")
    @Temporal(TemporalType.DATE)
    private Date addTime;// timestamp not null default current_timestamp comment '添加时间',

    @Column(name = "updater_no")
    private String updaterNo = "999999";//varchar(50) not null comment '更新人编号',

    @Column(name = "updater_name")
    private String updaterName = "SYSTEM";//varchar(50) not null comment '更新人姓名',

    @Column(name = "update_time")
    @Temporal(TemporalType.DATE)
    private Date updateTime;//timestamp not null default current_timestamp on update current_timestamp comment '更新时间',
}
