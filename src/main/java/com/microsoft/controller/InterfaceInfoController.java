package com.microsoft.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.microsoft.annotation.AuthCheck;
import com.microsoft.commen.DeleteRequest;
import com.microsoft.commen.ErrorCode;
import com.microsoft.commen.Result;
import com.microsoft.config.FrankApiGatewayConfig;
import com.microsoft.exception.BusinessException;
import com.microsoft.frankapisdk.client.FrankApiClient;
import com.microsoft.model.dto.interfaceinfo.InterfaceInfoAddRequest;
import com.microsoft.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.microsoft.model.dto.interfaceinfo.InterfaceInfoIdRequest;
import com.microsoft.model.dto.interfaceinfo.InterfaceInfoUpdateRequest;
import com.microsoft.model.entity.InterfaceInfo;
import com.microsoft.model.entity.UserPaymentAkSk;
import com.microsoft.model.enums.InterfaceInfoStatusEnum;
import com.microsoft.model.vo.InterfaceInfoVO;
import com.microsoft.service.InterfaceInfoService;
import com.microsoft.service.UserPaymentAkSkService;
import com.microsoft.utils.CurrentHold;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import static com.microsoft.constant.ErrorDescriptionConstant.*;
import static com.microsoft.constant.UserConstant.ADMIN_ROLE;

@Tag(name = "接口信息模块", description = "接口信息的增删改查接口")
@Slf4j
@RestController
@RequestMapping("/interfaceInfo")
public class InterfaceInfoController {
    @Resource
    private InterfaceInfoService interfaceInfoService;
    @Resource
    private UserPaymentAkSkService userPaymentAkSkService;
    // 网关的地址
    @Resource
    private FrankApiGatewayConfig frankApiGatewayConfig;

    /**
     * 添加一个接口
     */
    @AuthCheck(mustRole = ADMIN_ROLE)
    @PostMapping("/add")
    public Result<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest addRequest) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, PARAM_EMPTY);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(addRequest, interfaceInfo);
        // 校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        Long userId = CurrentHold.getCurrentId();
        interfaceInfo.setUserId(userId);
        boolean save = interfaceInfoService.save(interfaceInfo);
        if (!save) {
            throw new BusinessException(ErrorCode.DATABASE_ERROR, DATABASE_INSERT_FAILED);
        }
        Long interfaceInfoId = interfaceInfo.getId();
        return Result.success(interfaceInfoId);
    }

    /**
     * 删除一个接口
     */
    @AuthCheck(mustRole = ADMIN_ROLE)
    @PostMapping("/delete")
    public Result<Void> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, PARAM_INVALID);
        }
        // 删除之前先查询是否存在
        InterfaceInfo getById = interfaceInfoService.getById(deleteRequest.getId());
        if (getById == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, OBJECT_NOT_FOUND);
        }
        boolean removeById = interfaceInfoService.removeById(deleteRequest.getId());
        if (!removeById) {
            throw new BusinessException(ErrorCode.DATABASE_ERROR, DATABASE_DELETE_FAILED);
        }
        return Result.success();
    }

    /**
     * 修改接口
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public Result<Void> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest updateRequest) {
        if (updateRequest == null || updateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, PARAM_INVALID);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(updateRequest, interfaceInfo);
        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
        // 判断是否存在接口
        InterfaceInfo byId = interfaceInfoService.getById(interfaceInfo.getId());
        if (byId == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, OBJECT_NOT_FOUND);
        }
        boolean updateById = interfaceInfoService.updateById(interfaceInfo);
        if (!updateById) {
            throw new BusinessException(ErrorCode.DATABASE_ERROR, DATABASE_UPDATE_FAILED);
        }
        return Result.success();
    }

    /**
     * 发布接口
     */
    @PostMapping("/release")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public Result<Void> releaseInterface(@RequestBody InterfaceInfoIdRequest releaseRequest) {
        if (releaseRequest == null || releaseRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, PARAM_INVALID);
        }
        // 校验接口是否存在
        Long interfaceId = releaseRequest.getId();
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(interfaceId);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, OBJECT_NOT_FOUND);
        }
        // ----------------------------------------------------
        // 查询到管理员的ak,sk，用该ak,sk调用接口
        Long userId = CurrentHold.getCurrentId();
        // 调用 Service 层方法获取有效的 AK/SK
        UserPaymentAkSk paymentAkSk = userPaymentAkSkService.getValidAkSk(userId);
        // 使用管理员的 AK/SK 调用接口
        String baseUrl = frankApiGatewayConfig.getBaseUrl();
        FrankApiClient frankApiClient = new FrankApiClient(baseUrl,
                paymentAkSk.getAccessKey(),
                paymentAkSk.getSecretKeyHash());
        // ----------------------------------------------------
        // 拿到接口请求路径
        String interfaceInfoPath = interfaceInfo.getUrl();
        // 校验接口是否能够请求
        try {
            if (!frankApiClient.testConnection(interfaceInfoPath)) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, INTERFACE_CONNECTION_ERROR);
            }
        } catch (Exception e) {
            log.error("Exception : {}, message : {}", e, e.getMessage());
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, INTERFACE_CONNECTION_ERROR);
        }
        // 修改接口状态为 发布
        UpdateWrapper<InterfaceInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", interfaceId);
        updateWrapper.set("status", InterfaceInfoStatusEnum.RELEASE.getValue());
        boolean updateResult = interfaceInfoService.update(updateWrapper);
        if (!updateResult) {
            throw new BusinessException(ErrorCode.DATABASE_ERROR, DATABASE_UPDATE_FAILED);
        }
        return Result.success();
    }

    /**
     * 下线接口
     */
    @PostMapping("/offline")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public Result<Void> offlineInterface(@RequestBody InterfaceInfoIdRequest offlineRequest) {
        if (offlineRequest == null || offlineRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, PARAM_INVALID);
        }
        // 校验接口是否存在
        Long interfaceId = offlineRequest.getId();
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(interfaceId);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, OBJECT_NOT_FOUND);
        }
        // 修改接口状态为 0
        UpdateWrapper<InterfaceInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", interfaceId);
        updateWrapper.set("status", InterfaceInfoStatusEnum.OFFLINE.getValue());
        boolean updateResult = interfaceInfoService.update(updateWrapper);
        if (!updateResult) {
            throw new BusinessException(ErrorCode.DATABASE_ERROR, DATABASE_UPDATE_FAILED);
        }
        return Result.success();
    }

    /**
     * 查询接口 根据接口id查询回显
     */
    @GetMapping("/get/vo")
    public Result<InterfaceInfoVO> getInterfaceInfoVOById(Long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, PARAM_FORMAT_ERROR);
        }
        InterfaceInfo byId = interfaceInfoService.getById(id);
        if (byId == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, OBJECT_NOT_FOUND);
        }
        return Result.success(interfaceInfoService.getInterfaceInfoVO(byId));
    }

    /**
     * 分页查询（管理员）
     */
    @AuthCheck(mustRole = ADMIN_ROLE)
    @PostMapping("/list/page")
    public Result<Page<InterfaceInfo>> listInterfaceInfoByPage(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, PARAM_EMPTY);
        }
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size),
                interfaceInfoService.getQueryWrapper(interfaceInfoQueryRequest));
        return Result.success(interfaceInfoPage);
    }

    /**
     * 分页查询（VO）
     */
    @PostMapping("/list/page/vo")
    public Result<Page<InterfaceInfoVO>> listInterfaceInfoVOByPage(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, PARAM_EMPTY);
        }
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        // 限制爬虫
        if (size > 20) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, PAGE_SIZE_EXCEEDED);
        }
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size),
                interfaceInfoService.getQueryWrapper(interfaceInfoQueryRequest));
        return Result.success(interfaceInfoService.getInterfaceInfoVOPage(interfaceInfoPage));
    }
}
