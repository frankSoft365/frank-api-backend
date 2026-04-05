package com.microsoft.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 保存用户ak、sk与有效性
 */
@Data
@TableName("user_payment_aksk") // 映射合并表
public class UserPaymentAkSk {
    @TableId(type = IdType.AUTO) // 自增主键
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("service_start_time")
    private LocalDateTime serviceStartTime;
    
    @TableField("service_end_time")
    private LocalDateTime serviceEndTime;
    
    // AK/SK字段（未付费为NULL）
    @TableField("access_key")
    private String accessKey;
    
    @TableField("secret_key_hash")
    private String secretKeyHash;
    
    @TableField("secret_key_salt")
    private String secretKeySalt;
    
    @TableField("aksk_status")
    private Integer akskStatus;

    @TableField("aksk_view_status")
    private Integer akskViewStatus;
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
}