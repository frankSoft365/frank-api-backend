package com.microsoft.model.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 记录每个用户、每个接口的成功调用总次数
 */
@Data
public class UserInterfaceCallCount {
    // 日志id
    private Long id;
    // 调用者ID
    private Long userId;
    // 所调用的接口ID
    private Long interfaceInfoId;
    // 该用户该接口成功调用总次数
    private Integer totalCount;
    // 日志创建时间
    private LocalDateTime createTime;
    // 日志修改时间
    private LocalDateTime updateTime;
}
