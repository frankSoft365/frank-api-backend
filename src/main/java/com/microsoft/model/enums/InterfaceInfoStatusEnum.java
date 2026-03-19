package com.microsoft.model.enums;

import lombok.Getter;

@Getter
public enum InterfaceInfoStatusEnum {
    RELEASE(1, "发布"),
    OFFLINE(0, "下线");

    private final int value;
    private final String name;

    InterfaceInfoStatusEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }
}
