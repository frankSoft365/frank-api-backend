package com.microsoft.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserInterfaceCallCountMapper {

    /**
     * 插入 or 更新 +1
     */
    void insertOrUpdateCount(@Param("userId") Long userId, @Param("interfaceInfoId") Long interfaceInfoId);

}
