package com.microsoft.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserPaymentAkSkStatusEnum {
    CAN_VIEW(0, "可查看"),
    CANNOT_VIEW(1, "不可查看");
    
    private final Integer value;
    private final String name;
}
