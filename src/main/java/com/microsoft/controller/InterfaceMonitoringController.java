package com.microsoft.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.microsoft.annotation.AuthCheck;
import com.microsoft.commen.ErrorCode;
import com.microsoft.commen.Result;
import com.microsoft.constant.ErrorDescriptionConstant;
import com.microsoft.exception.BusinessException;
import com.microsoft.model.dto.interfaceMonitoring.InterfaceStatQueryDTO;
import com.microsoft.model.dto.interfaceMonitoring.MonitorOverviewQueryDTO;
import com.microsoft.model.dto.interfaceMonitoring.UserCallRankQueryDTO;
import com.microsoft.model.dto.interfaceMonitoring.UserInterfaceLogQueryDTO;
import com.microsoft.model.vo.interfaceMonitoring.*;
import com.microsoft.service.InterfaceMonitoringService;
import com.microsoft.utils.CurrentHold;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 用户调用总览
     */
    @PostMapping("/overview/user")
    public Result<MonitorOverviewVO> getUserMonitorOverviewVO(
            @RequestBody MonitorOverviewQueryDTO queryDTO
    ) {
        Long userId = CurrentHold.getCurrentId();
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, ErrorDescriptionConstant.PARAM_INVALID);
        }
        MonitorOverviewVO userMonitorOverviewVO = interfaceMonitoringService.getUserMonitorOverviewVO(queryDTO, userId);
        return Result.success(userMonitorOverviewVO);
    }

    /**
     * 用户调用日志
     */
    @PostMapping("/interfaceLog/user")
    public Result<IPage<UserInterfaceLogVO>> getUserInterfaceLogVO(
            @RequestBody UserInterfaceLogQueryDTO queryDTO
    ) {
        Long userId = CurrentHold.getCurrentId();
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, ErrorDescriptionConstant.PARAM_INVALID);
        }
        IPage<UserInterfaceLogVO> userInterfaceLogVOList = interfaceMonitoringService.getUserInterfaceLogVOList(queryDTO, userId);
        return Result.success(userInterfaceLogVOList);
    }

    /**
     * 用户调用分布
     */
    @GetMapping("/callDistribution/user")
    public Result<UserCallDistributionVO> getUserCallDistributionVO() {
        Long userId = CurrentHold.getCurrentId();
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, ErrorDescriptionConstant.PARAM_INVALID);
        }
        UserCallDistributionVO userCallDistributionVO = interfaceMonitoringService.getUserCallDistributionVO(userId);
        return Result.success(userCallDistributionVO);
    }
}
