package com.microsoft.service.impl.rpc;

import com.microsoft.frankapisdk.service.InnerUserInterfaceInfoService;
import com.microsoft.frankapisdk.service.model.dto.ApiCallLoggingDTO;
import com.microsoft.mapper.UserInterfaceCallCountMapper;
import com.microsoft.service.InterfaceLogService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    @Resource
    private UserInterfaceCallCountMapper userInterfaceCallCountMapper;
    @Resource
    private InterfaceLogService interfaceLogService;

    public void incrementCount(Long userId, Long interfaceInfoId) {
        userInterfaceCallCountMapper.insertOrUpdateCount(userId, interfaceInfoId);
    }

    @Override
    public void apiCallLogging(ApiCallLoggingDTO apiCallLoggingDTO) {
        interfaceLogService.addALogEntry(apiCallLoggingDTO);
    }
}