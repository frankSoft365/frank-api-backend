package com.microsoft.mapper;

import com.microsoft.model.vo.interfaceMonitoring.InterfacePathCountVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserInterfaceCallCountMapper {

    /**
     * 插入 or 更新 +1
     */
    void insertOrUpdateCount(@Param("userId") Long userId, @Param("interfaceInfoId") Long interfaceInfoId);

    /**
     * 查询指定用户的总调用次数
     */
    Integer selectUserTotalCount(@Param("userId") Long userId);

    /**
     * 查询指定用户不同接口的调用次数 map
     */
    List<InterfacePathCountVO> selectCallDistribution(@Param("userId")Long userId);
}
