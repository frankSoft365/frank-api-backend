package com.microsoft.model.dto.interfaceMonitoring;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MonitorOverviewQueryDTO {
    // 开始时间
    private LocalDateTime startTime;
    // 结束时间
    private LocalDateTime endTime;
    // 查询时间
    private LocalDateTime queryTime;
}