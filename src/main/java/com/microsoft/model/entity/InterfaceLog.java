package com.microsoft.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 接口请求日志
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("`interface_log`")
public class InterfaceLog {
    // 日志id
    private Long id;
    // 全局唯一的请求ID 用于精确标记用户的每一次请求
    private String requestId;
    // 用户请求的接口的路径：/api/...
    private String interfaceUrl;
    // 接口请求方法：GET POST ...
    private String requestMethod;
    // 请求者ip地址
    private String hostIp;
    // 耗时 从发送请求到网关到准备返回的处理时间
    private Integer costTime;
    // 该次请求是否成功 即标准API响应是否含有Error字段 1：成功 0：失败
    private Integer success;
    // 失败原因
    private String errorMessage;
    // 日志记录时间
    private LocalDateTime createTime;
}
