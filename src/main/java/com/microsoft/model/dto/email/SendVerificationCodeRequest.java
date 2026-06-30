package com.microsoft.model.dto.email;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "发送邮箱验证码-请求参数", requiredProperties = {"email"})
public class SendVerificationCodeRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "接收验证码的邮箱地址", example = "frankzhen2025@outlook.com")
    private String email;
}
