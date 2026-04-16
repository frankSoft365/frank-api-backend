package com.microsoft.model.vo.interfaceMonitoring;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeValueVO {
    private String time;  // 日期：如 04-01 或 12:00
    private Long value;   // 调用次数
}