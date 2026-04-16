package com.microsoft.model.vo.interfaceMonitoring;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonitorOverviewVO {

    // 顶部卡片
    private Long totalCall;    // 总调用次数
    private Double successRate;// 成功率 %
    private Double avgCost;    // 平均耗时 ms

    // 今日/近7天 调用量趋势
    private List<TimeValueVO> callTrend;
}