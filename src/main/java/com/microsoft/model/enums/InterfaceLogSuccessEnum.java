package com.microsoft.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InterfaceLogSuccessEnum {
    ERROR(0, "失败"),
    SUCCESS(1, "成功");

    private final Integer value;
    private final String name;
}
