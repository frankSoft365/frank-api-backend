package com.microsoft.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.microsoft.annotation.AuthCheck;
import com.microsoft.commen.DeleteRequest;
import com.microsoft.commen.ErrorCode;
import com.microsoft.commen.Result;
import com.microsoft.exception.BusinessException;
import com.microsoft.model.dto.interfaceinfo.InterfaceInfoAddRequest;
import com.microsoft.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.microsoft.model.dto.interfaceinfo.InterfaceInfoUpdateRequest;
import com.microsoft.model.entity.InterfaceInfo;
import com.microsoft.model.vo.InterfaceInfoVO;
import com.microsoft.service.InterfaceInfoService;
import com.microsoft.utils.CurrentHold;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import static com.microsoft.constant.UserConstant.ADMIN_ROLE;

@Tag(name = "接口信息模块", description = "接口信息的增删改查接口")
@Slf4j
@RestController
@RequestMapping("/interfaceInfo")
public class InterfaceInfoController {
    /**
     * 增删改查
     */
    @Resource
    private InterfaceInfoService interfaceInfoService;

    /**
     * 添加一个接口
     */
    @AuthCheck(mustRole = ADMIN_ROLE)
    @PostMapping("/add")
    public Result<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest addRequest) {
        if (addRequest == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "无添加接口信息");
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(addRequest, interfaceInfo);
        // 校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
        Long userId = CurrentHold.getCurrentId();
        interfaceInfo.setUserId(userId);
        interfaceInfoService.save(interfaceInfo);
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
            throw new BusinessException(ErrorCode.PARAM_ERROR, "未指定删除对象！");
        }
        // 删除之前先查询是否存在
        InterfaceInfo getById = interfaceInfoService.getById(deleteRequest.getId());
        if (getById == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "找不到所要删除接口！");
        }
        boolean removeById = interfaceInfoService.removeById(deleteRequest.getId());
        if (!removeById) {
            throw new BusinessException(ErrorCode.DATABASE_ERROR, "删除失败！");
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
            throw new BusinessException(ErrorCode.PARAM_ERROR, "找不到修改数据");
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(updateRequest, interfaceInfo);
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        // 判断是否存在接口
        InterfaceInfo byId = interfaceInfoService.getById(interfaceInfo.getId());
        if (byId == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "修改的接口不存在");
        }
        boolean updateById = interfaceInfoService.updateById(interfaceInfo);
        if (!updateById) {
            throw new BusinessException(ErrorCode.DATABASE_ERROR, "修改失败！");
        }
        return Result.success();
    }

    /**
     * 查询接口 根据接口id查询回显
     */
    @AuthCheck(mustRole = ADMIN_ROLE)
    @GetMapping("/get/vo")
    public Result<InterfaceInfoVO> getInterfaceInfoVOById(Long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "无法获取接口信息！");
        }
        InterfaceInfo byId = interfaceInfoService.getById(id);
        if (byId == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "无法获取接口信息");
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
            throw new BusinessException(ErrorCode.PARAM_ERROR, "无法查询！");
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
            throw new BusinessException(ErrorCode.PARAM_ERROR, "无法查询！");
        }
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        // 限制爬虫
        if (size > 20) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "不支持过大单页数据条数");
        }
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size),
                interfaceInfoService.getQueryWrapper(interfaceInfoQueryRequest));
        return Result.success(interfaceInfoService.getInterfaceInfoVOPage(interfaceInfoPage));
    }
}
