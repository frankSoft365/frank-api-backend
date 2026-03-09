package com.microsoft.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GenerateAkSkVO {
    private String accessKey;
    private String secretKey;
    private LocalDateTime expireTime;
    private Long userId;
}