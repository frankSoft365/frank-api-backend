package com.microsoft.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("`interface_info`")
public class InterfaceInfo {
    private Long id;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isDelete;
    private String description;
    private String name;
    private String url;
    private String requestHeader;
    private String responseHeader;
    private String requestParam;
    private Integer status;
    private String method;
    private Long userId;
}
