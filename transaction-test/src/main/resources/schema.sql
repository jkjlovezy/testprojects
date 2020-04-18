drop table if exists user;
drop table if exists goods;

create table user
(
    id       int not null auto_increment comment '主键',
    username varchar(100) comment '',
    password varchar(100) comment '',
    primary key (id)
);

create table goods
(
    id       int not null auto_increment comment '主键',
    name varchar(100) comment '',
    type varchar(100) comment '',
    primary key (id)
);

insert into user(username, password) values ( 'jkj','1234' );
