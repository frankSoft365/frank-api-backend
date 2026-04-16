package com.microsoft.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.microsoft.annotation.AuthCheck;
import com.microsoft.commen.Result;
import com.microsoft.model.dto.interfaceMonitoring.InterfaceStatQueryDTO;
import com.microsoft.model.dto.interfaceMonitoring.MonitorOverviewQueryDTO;
import com.microsoft.model.dto.interfaceMonitoring.UserCallRankQueryDTO;
import com.microsoft.model.vo.interfaceMonitoring.InterfaceStatVO;
import com.microsoft.model.vo.interfaceMonitoring.MonitorOverviewVO;
import com.microsoft.model.vo.interfaceMonitoring.UserCallRankVO;
import com.microsoft.service.InterfaceMonitoringService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.microsoft.constant.UserConstant.ADMIN_ROLE;

@RestController
@RequestMapping("/interfaceMonitoring")
public class InterfaceMonitoringController {
    @Resource
    private InterfaceMonitoringService interfaceMonitoringService;

    /**
     * 调用总览
     */
    @AuthCheck(mustRole = ADMIN_ROLE)
    @PostMapping("/overview/admin")
    public Result<MonitorOverviewVO> getAdminMonitorOverviewVO(
            @RequestBody MonitorOverviewQueryDTO queryDTO) {
        MonitorOverviewVO overviewVO = interfaceMonitoringService.getAdminMonitorOverviewVO(queryDTO);
        return Result.success(overviewVO);
    }

    /**
     * 具体接口信息
     */
    @AuthCheck(mustRole = ADMIN_ROLE)
    @PostMapping("/interfaceStat/admin")
    public Result<IPage<InterfaceStatVO>> getAdminInterfaceStatVO(
            @RequestBody InterfaceStatQueryDTO queryDTO
            ) {
        IPage<InterfaceStatVO> interfaceStatVOList = interfaceMonitoringService.getAdminInterfaceStatVOList(queryDTO);
        return Result.success(interfaceStatVOList);
    }

    /**
     * 用户调用排行信息
     */
    @AuthCheck(mustRole = ADMIN_ROLE)
    @PostMapping("/userCallRank/admin")
    public Result<IPage<UserCallRankVO>> getAdminUserCallRankVO(
            @RequestBody UserCallRankQueryDTO queryDTO
    ) {
        IPage<UserCallRankVO> userCallRankVOList = interfaceMonitoringService.getAdminUserCallRankVOList(queryDTO);
        return Result.success(userCallRankVOList);
    }
}
