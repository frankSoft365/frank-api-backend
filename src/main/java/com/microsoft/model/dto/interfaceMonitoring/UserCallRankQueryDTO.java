package com.microsoft.model.dto.interfaceMonitoring;

import com.microsoft.commen.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCallRankQueryDTO extends PageRequest {
    //    /**
    //     * 当前页号
    //     */
    //    private int current = 1;
    //    /**
    //     * 页面大小
    //     */
    //    private int pageSize = 10;
    // 开始时间
    private LocalDateTime startTime;
    // 结束时间
    private LocalDateTime endTime;
    // 查询时间
    private LocalDateTime queryTime;
    // 用户id
    private Long userId;
}
