package com.microsoft.controller;

import com.microsoft.service.UserPaymentAkSkService;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务：每天凌晨1点禁用超期AK/SK
 */
@Component
public class AkSkExpireJob {
    @Resource
    private UserPaymentAkSkService paymentAkSkService;

    @Scheduled(cron = "0 0 1 * * ?")
    public void disableExpiredAkSk() {
        paymentAkSkService.disableExpiredAkSk();
    }
}