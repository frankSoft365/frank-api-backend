package com.microsoft.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.microsoft.model.entity.InterfaceLog;
import com.microsoft.model.vo.interfaceMonitoring.InterfaceStatVO;
import com.microsoft.model.vo.interfaceMonitoring.UserCallRankVO;
import com.microsoft.model.vo.interfaceMonitoring.UserInterfaceLogVO;
import org.apache.ibatis.annotations.Param;


public interface InterfaceLogMapper extends BaseMapper<InterfaceLog> {

    /**
     * 接口调用状态表
     */
    IPage<InterfaceStatVO> selectInterfaceStatVOList(Page<InterfaceStatVO> page, @Param("ew") Wrapper<InterfaceLog> wrapper);

    /**
     * 获取用户调用排行列表
     */
    IPage<UserCallRankVO> selectUserCallRankVOList(Page<UserCallRankVO> page, @Param("ew") Wrapper<InterfaceLog> wrapper);

    /**
     * 用户的接口调用日志
     */
    IPage<UserInterfaceLogVO> selectUserInterfaceLogVOList(Page<UserInterfaceLogVO> page, @Param("ew") Wrapper<InterfaceLog> wrapper);

}
