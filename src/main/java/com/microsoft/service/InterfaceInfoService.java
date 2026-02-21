package com.microsoft.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.microsoft.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.microsoft.model.entity.InterfaceInfo;
import com.microsoft.model.vo.InterfaceInfoVO;

public interface InterfaceInfoService extends IService<InterfaceInfo> {

    /**
     * 添加接口时校验字段合法
     */
    void validInterfaceInfo(InterfaceInfo interfaceInfo, Boolean add);

    /**
     * 获取接口VO
     */
    InterfaceInfoVO getInterfaceInfoVO(InterfaceInfo byId);

    /**
     * 获取查询条件
     */
    Wrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest interfaceInfoQueryRequest);

    /**
     * 获取分页接口VO
     */
    Page<InterfaceInfoVO> getInterfaceInfoVOPage(Page<InterfaceInfo> interfaceInfoPage);
}
