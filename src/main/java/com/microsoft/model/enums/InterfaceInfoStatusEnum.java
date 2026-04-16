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

    /**
     * 根据 value 判断是否存在该状态
     * @param value 状态值
     * @return true-存在，false-不存在
     */
    public static boolean isValidValue(int value) {
        for (InterfaceInfoStatusEnum statusEnum : values()) {
            if (statusEnum.value == value) {
                return true;
            }
        }
        return false;
    }
}
