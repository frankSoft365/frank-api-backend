package com.microsoft.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.microsoft.commen.ErrorCode;
import com.microsoft.exception.BusinessException;
import com.microsoft.mapper.InterfaceInfoMapper;
import com.microsoft.mapper.UserMapper;
import com.microsoft.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.microsoft.model.entity.InterfaceInfo;
import com.microsoft.model.entity.User;
import com.microsoft.model.vo.InterfaceInfoVO;
import com.microsoft.service.InterfaceInfoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo> implements InterfaceInfoService {

    @Resource
    private UserMapper userMapper;

    /**
     * 校验添加接口信息
     */
    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, Boolean add) {
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "无添加接口信息");
        }
        String name = interfaceInfo.getName();
        String url = interfaceInfo.getUrl();
        String requestHeader = interfaceInfo.getRequestHeader();
        String responseHeader = interfaceInfo.getResponseHeader();
        String description = interfaceInfo.getDescription();
        String method = interfaceInfo.getMethod();
        Integer status = interfaceInfo.getStatus();
        // 必填项：（接口状态是有默认值必填项）
        if (StringUtils.isAnyBlank(name, url, method)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "接口名称、地址、请求方法是必填项！");
        }
        if (!add) {
            if (!Integer.valueOf(0).equals(status) && !Integer.valueOf(1).equals(status)) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, "接口状态错误！");
            }
        }
        // 非必填项：
        if (StringUtils.isNotBlank(requestHeader) && requestHeader.length() > 15000) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "请求头过长！");
        }
        if (StringUtils.isNotBlank(responseHeader) && responseHeader.length() > 15000) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "响应头过长！");
        }
        if (StringUtils.isNotBlank(description) && description.length() > 256) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "接口描述过长！");
        }
    }

    @Override
    public InterfaceInfoVO getInterfaceInfoVO(InterfaceInfo interfaceInfo) {
        InterfaceInfoVO interfaceInfoVO = InterfaceInfoVO.objToVo(interfaceInfo);
        // 创建人昵称
        Long userId = interfaceInfoVO.getUserId();
        String username = null;
        if (userId != null && userId > 0) {
            username = userMapper.selectById(userId).getUsername();
        }
        interfaceInfoVO.setUsername(username);
        return interfaceInfoVO;
    }

    /**
     * 获取查询条件
     */
    @Override
    public Wrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        String name = interfaceInfoQueryRequest.getName();
        String url = interfaceInfoQueryRequest.getUrl();
        String description = interfaceInfoQueryRequest.getDescription();
        Integer status = interfaceInfoQueryRequest.getStatus();
        if (status != null && !Integer.valueOf(0).equals(status) && !Integer.valueOf(1).equals(status)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "接口状态错误！");
        }
        String method = interfaceInfoQueryRequest.getMethod();
        if (StringUtils.isNotBlank(name)) {
            queryWrapper.like("name", name);
        }
        if (StringUtils.isNotBlank(url)) {
            queryWrapper.like("url", url);
        }
        if (StringUtils.isNotBlank(description)) {
            queryWrapper.like("description", description);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        if (StringUtils.isNotBlank(method)) {
            queryWrapper.like("method", method);
        }
        queryWrapper.orderByDesc("update_time");
        return queryWrapper;
    }

    @Override
    public Page<InterfaceInfoVO> getInterfaceInfoVOPage(Page<InterfaceInfo> interfaceInfoPage) {
        List<InterfaceInfo> interfaceInfoList = interfaceInfoPage.getRecords();
        Page<InterfaceInfoVO> interfaceInfoVOPage = new Page<>(interfaceInfoPage.getCurrent(), interfaceInfoPage.getSize(), interfaceInfoPage.getTotal());
        if (CollectionUtils.isEmpty(interfaceInfoList)) {
            return interfaceInfoVOPage;
        }
        // 先查处所需的创建人
        Set<Long> neededUserId = interfaceInfoList.stream().map(InterfaceInfo::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> neededUser = userMapper.selectBatchIds(neededUserId).stream().collect(Collectors.groupingBy(User::getId));
        List<InterfaceInfoVO> interfaceInfoVOList = interfaceInfoList.stream().map(item -> {
            InterfaceInfoVO interfaceInfoVO = InterfaceInfoVO.objToVo(item);
            Long userId = interfaceInfoVO.getUserId();
            if (neededUser.containsKey(userId)) {
                interfaceInfoVO.setUsername(neededUser.get(userId).get(0).getUsername());
            }
            return interfaceInfoVO;
        }).toList();
        interfaceInfoVOPage.setRecords(interfaceInfoVOList);
        return interfaceInfoVOPage;
    }
}
