package com.microsoft.model.vo.interfaceMonitoring;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserCallRankVO {
    private Long userId;        // 用户ID
    private Long totalCall;     // 总调用次数
    private Long interfaceCount; // 调用接口数
    private Double avgCost; // 平均耗时
    private Double successRate;// 成功率 %
    private LocalDateTime lastCallTime; // 最后调用时间
}