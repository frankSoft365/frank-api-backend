package com.microsoft.model.dto.userPaymentAkSk;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GenerateAkSkRequest {
    @NotBlank(message = "用户ID不能为空")
    private Long userId;
    private Integer payDays;
}