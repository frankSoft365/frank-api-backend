package com.microsoft.model.vo.interfaceMonitoring;

import lombok.Data;

import java.util.List;

@Data
public class UserCallDistributionVO {
    private Integer totalSuccessCount;
    private List<InterfacePathCountVO> callDistribution;
}
