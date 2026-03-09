package com.microsoft.controller;

import com.microsoft.commen.Result;
import com.microsoft.model.dto.userPaymentAkSk.GenerateAkSkRequest;
import com.microsoft.model.vo.GenerateAkSkVO;
import com.microsoft.service.UserPaymentAkSkService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/userPaymentAkSk")
public class UserPaymentAkSkController {
    @Resource
    private UserPaymentAkSkService paymentAkSkService;

    /**
     * 付费并分配AK/SK接口
     */
    @PostMapping("/pay-and-generate")
    public Result<GenerateAkSkVO> payAndGenerateAkSk(@Validated @RequestBody GenerateAkSkRequest request) {
        GenerateAkSkVO response = paymentAkSkService.payAndGenerateAkSk(request);
        return Result.success(response);
    }
}