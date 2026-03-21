package com.microsoft.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckViewChanceVO {
    private Boolean hasChance;
    private String message;
}
