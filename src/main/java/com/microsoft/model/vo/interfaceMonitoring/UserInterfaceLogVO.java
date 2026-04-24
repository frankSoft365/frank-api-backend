package com.microsoft.model.vo.interfaceMonitoring;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserInterfaceLogVO {
    private String requestId;
    private LocalDateTime requestTime;
    private String interfacePath;
    private String requestMethod;
    private Integer success; // 1 成功 0 失败
    private Long costTime;
    private String errorMessage;
}
