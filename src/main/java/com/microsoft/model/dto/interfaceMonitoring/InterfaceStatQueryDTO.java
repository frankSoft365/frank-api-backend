package com.microsoft.model.dto.interfaceMonitoring;

import com.microsoft.commen.PageRequest;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InterfaceStatQueryDTO extends PageRequest {
    // 开始时间
    private LocalDateTime startTime;
    // 结束时间
    private LocalDateTime endTime;
    // 查询时间
    private LocalDateTime queryTime;
    // 查询路径 模糊查询
    private String interfacePath;
    // 请求方法 POST&GET...
    private String requestMethod;
    // 请求结果 0成功&1失败&null全选
    private Integer requestResult;
}
