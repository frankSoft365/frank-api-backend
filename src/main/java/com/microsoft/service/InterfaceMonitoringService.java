package com.microsoft.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.microsoft.model.dto.interfaceMonitoring.InterfaceStatQueryDTO;
import com.microsoft.model.dto.interfaceMonitoring.MonitorOverviewQueryDTO;
import com.microsoft.model.dto.interfaceMonitoring.UserCallRankQueryDTO;
import com.microsoft.model.vo.interfaceMonitoring.InterfaceStatVO;
import com.microsoft.model.vo.interfaceMonitoring.MonitorOverviewVO;
import com.microsoft.model.vo.interfaceMonitoring.UserCallRankVO;

import java.util.List;

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
}
