create database if not exists `frank-api`;
use `frank-api`;
create table interface_info
(
    id              bigint unsigned auto_increment comment '主键'
        primary key,
    create_time     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    is_delete       tinyint  default 0                 not null comment '逻辑删除 0 未删除 1 已经删除',
    description     varchar(256)                       null comment '接口描述',
    name            varchar(256)                       not null comment '接口名称',
    url             varchar(512)                       not null comment '接口地址',
    request_header  text                               null comment '请求头',
    response_header text                               null comment '响应头',
    request_param   text                               null comment '请求参数',
    status          tinyint  default 0                 not null comment '接口状态 0 关闭 1 开启',
    method          varchar(256)                       not null comment '请求方式',
    user_id         bigint unsigned                    not null comment '创建人',
    constraint uk_interface_info_url
        unique (url)
)
    comment '接口信息';

create table interface_log
(
    id             bigint unsigned auto_increment comment '主键'
        primary key,
    request_id     varchar(255)                       not null comment '请求唯一ID',
    interface_url  varchar(255)                       not null comment '请求接口',
    request_method varchar(16)                        not null comment 'GET/POST',
    user_id        bigint unsigned                    null comment '用户id',
    host_ip        varchar(64)                        not null comment '请求IP',
    cost_time      bigint unsigned                    not null comment '耗时ms',
    success        tinyint                            not null comment '1成功 0失败',
    error_message  varchar(255)                       null comment '失败原因：签名失败/无权限等',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间'
)
    comment '接口调用日志（只增不改）';

create index idx_create_time
    on interface_log (create_time);

create index idx_request_id
    on interface_log (request_id);

create table user
(
    username     varchar(20)                  null comment '用户名',
    id           bigint unsigned auto_increment comment '主键 自增'
        primary key,
    gender       tinyint unsigned             null comment '性别 1 男 2 女',
    phone        char(11)                     null comment '手机号',
    password     varchar(255)                 not null comment '密码',
    avatar       varchar(255)                 null comment '头像的url',
    create_time  datetime                     null comment '创建时间',
    update_time  datetime                     null comment '修改时间',
    is_delete    tinyint          default 0   null comment '是否删除 0 没有被删除 1 被删除了',
    user_status  tinyint unsigned default '1' null comment '是否有效 1 正常',
    email        varchar(255)                 null comment '邮箱',
    user_account varchar(255)                 not null comment '用户登录账号',
    role         tinyint          default 0   not null comment '用户角色 0 普通用户 1 管理员',
    constraint uk_email
        unique (email),
    constraint uk_phone
        unique (phone),
    constraint uk_user_account
        unique (user_account)
)
    comment '用户表';

create table user_interface_call_count
(
    id                bigint unsigned auto_increment comment '主键id'
        primary key,
    user_id           bigint unsigned                        not null comment '用户id',
    interface_info_id bigint unsigned                        not null comment '接口信息id',
    total_count       int unsigned default '1'               not null comment '调用总次数',
    create_time       datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time       datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    constraint idx_user_interface_info
        unique (user_id, interface_info_id)
)
    comment '不同用户调用不同接口次数的记录';

create table user_payment_aksk
(
    id                 bigint unsigned auto_increment
        primary key,
    user_id            bigint unsigned                    not null comment '关联用户ID（唯一）',
    service_start_time datetime                           null comment '服务开始时间（付费后赋值）',
    service_end_time   datetime                           null comment '服务结束时间（AK/SK有效期）',
    access_key         varchar(64)                        null comment 'AK（全局唯一，未付费为NULL）',
    secret_key_hash    varchar(256)                       null comment 'SK哈希（未付费为NULL）',
    secret_key_salt    varchar(32)                        null comment '盐值（未付费为NULL）',
    aksk_status        tinyint(1)                         null comment 'AK/SK状态：1-启用，0-禁用（未付费为NULL）',
    create_time        datetime default CURRENT_TIMESTAMP not null,
    update_time        datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    aksk_view_status   tinyint  default 0                 null comment '1-被查看 0-未查看',
    constraint uk_access_key
        unique (access_key),
    constraint uk_user_id
        unique (user_id)
)
    comment '付费+AK/SK合并表（未付费字段全为NULL）';

insert into user (username, password, user_account, role, email, user_status)
select 'admin', '25d55ad283aa400af464c76d713c07ad', 'admin888', 1, 'admin@frank-api.com', 1
where not exists (select 1 from user where user_account = 'admin888');




