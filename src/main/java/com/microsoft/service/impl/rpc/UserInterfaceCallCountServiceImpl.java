package com.microsoft.service.impl.rpc;


import com.microsoft.frankapisdk.service.UserInterfaceCallCountService;
import com.microsoft.mapper.UserInterfaceCallCountMapper;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class UserInterfaceCallCountServiceImpl implements UserInterfaceCallCountService {

    @Resource
    private UserInterfaceCallCountMapper userInterfaceCallCountMapper;

    public void incrementCount(Long userId, Long interfaceInfoId) {
        userInterfaceCallCountMapper.insertOrUpdateCount(userId, interfaceInfoId);
    }
}