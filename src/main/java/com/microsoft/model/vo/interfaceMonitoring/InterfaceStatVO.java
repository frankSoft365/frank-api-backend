package com.microsoft.model.vo.interfaceMonitoring;

import lombok.Data;

@Data
public class InterfaceStatVO {

    private String interfacePath; // 接口地址
    private String requestMethod; // 请求方法

    private Long total;     // 总调用次数
    private Long success;   // 成功次数
    private Long fail;      // 失败次数
    private Double successRate;// 成功率 %

    private Double avgCost; // 平均耗时
    private Long maxCost;   // 最大耗时
}