package com.microsoft.model.dto.interfaceinfo;

import com.microsoft.commen.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InterfaceInfoQueryRequest extends PageRequest {
    private String name;
    private String url;
    private String description;
    private Integer status;
    private String method;
}
