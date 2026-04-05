package com.microsoft.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 接口信息
 */
@Data
@TableName("`interface_info`")
public class InterfaceInfo {
    // 接口id
    private Long id;
    // 接口信息创建时间
    private LocalDateTime createTime;
    // 接口信息修改时间
    private LocalDateTime updateTime;
    // 逻辑删除 0:未删除 1:已经删除
    private Integer isDelete;
    // 接口信息功能描述
    private String description;
    // 接口名称
    private String name;
    // 接口路径：/api/...
    private String url;
    // 请求头
    private String requestHeader;
    // 响应头
    private String responseHeader;
    // 请求参数 openApi的标准描述json
    private String requestParam;
    // 接口状态 0:关闭/下线 1: 开启/发布
    private Integer status;
    // 接口请求方法：GET POST ...
    private String method;
    // 创建者的ID
    private Long userId;
}
