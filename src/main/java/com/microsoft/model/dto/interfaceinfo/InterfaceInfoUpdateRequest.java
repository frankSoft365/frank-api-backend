package com.microsoft.model.dto.interfaceinfo;

import lombok.Data;

@Data
public class InterfaceInfoUpdateRequest {
    // 要修改的接口的id
    private Long id;
    // 修改接口的名称
    private String name;
    // 修改接口的地址
    private String url;
    // 修改请求头和响应头
    private String requestHeader;
    private String responseHeader;
    // 修改请求参数
    private String requestParam;
    // 修改接口的描述信息
    private String description;
    // 修改接口的请求方法
    private String method;
}
