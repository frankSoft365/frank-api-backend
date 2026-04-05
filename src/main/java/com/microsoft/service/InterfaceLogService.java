package com.microsoft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.microsoft.model.entity.InterfaceLog;

public interface InterfaceLogService extends IService<InterfaceLog> {
    void addALogEntry(InterfaceLog log);
}
