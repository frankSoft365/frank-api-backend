package com.microsoft.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.microsoft.model.dto.interfaceMonitoring.InterfaceStatQueryDTO;
import com.microsoft.model.dto.interfaceMonitoring.MonitorOverviewQueryDTO;
import com.microsoft.model.dto.interfaceMonitoring.UserCallRankQueryDTO;
import com.microsoft.model.dto.interfaceMonitoring.UserInterfaceLogQueryDTO;
import com.microsoft.model.vo.interfaceMonitoring.*;

public interface InterfaceMonitoringService {
    /**
     * 管理员的接口调用信息总览
     */
    MonitorOverviewVO getAdminMonitorOverviewVO(MonitorOverviewQueryDTO monitorOverviewQueryDTO);

    /**
     * 管理员的接口状态表
     */
    IPage<InterfaceStatVO> getAdminInterfaceStatVOList(InterfaceStatQueryDTO interfaceStatQueryDTO);

    /**
     * 管理员的用户调用排行监控
     */
    IPage<UserCallRankVO> getAdminUserCallRankVOList(UserCallRankQueryDTO userCallRankQueryDTO);

    /**
     * 用户的接口调用信息总览
     */
    MonitorOverviewVO getUserMonitorOverviewVO(MonitorOverviewQueryDTO monitorOverviewQueryDTO, Long userId);

    /**
     * 用户的接口调用日志表
     */
    IPage<UserInterfaceLogVO> getUserInterfaceLogVOList(UserInterfaceLogQueryDTO userInterfaceLogQueryDTO, Long userId);

    /**
     * 用户的接口调用分布
     */
    UserCallDistributionVO getUserCallDistributionVO(Long userId);

}
