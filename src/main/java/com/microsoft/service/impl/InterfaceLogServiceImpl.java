package com.microsoft.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.microsoft.commen.ErrorCode;
import com.microsoft.exception.BusinessException;
import com.microsoft.mapper.InterfaceLogMapper;
import com.microsoft.model.entity.InterfaceLog;
import com.microsoft.service.InterfaceLogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InterfaceLogServiceImpl extends ServiceImpl<InterfaceLogMapper, InterfaceLog> implements InterfaceLogService {
    @Resource
    private InterfaceLogMapper interfaceLogMapper;
    @Override
    public void addALogEntry(InterfaceLog log) {
        String requestId = log.getRequestId();
        String interfaceUrl = log.getInterfaceUrl();
        String requestMethod = log.getRequestMethod();
        String hostIp = log.getHostIp();
        Integer costTime = log.getCostTime();
        Integer success = log.getSuccess();
        String errorMessage = log.getErrorMessage();
        InterfaceLog interfaceLog = new InterfaceLog();
        interfaceLog.setRequestId(requestId);
        interfaceLog.setInterfaceUrl(interfaceUrl);
        interfaceLog.setRequestMethod(requestMethod);
        interfaceLog.setHostIp(hostIp);
        interfaceLog.setCostTime(costTime);
        interfaceLog.setSuccess(success);
        interfaceLog.setErrorMessage(errorMessage);
        int insert = interfaceLogMapper.insert(log);
        if (insert == 0) {
            throw new BusinessException(ErrorCode.DATABASE_ERROR, "请求日志记录失败");
        }
    }
}
