package com.microsoft.model.dto.interfaceinfo;

import lombok.Data;

@Data
public class InterfaceInfoAddRequest {
    private String name;
    private String url;
    private String requestHeader;
    private String responseHeader;
    private String description;
    private String method;
}
