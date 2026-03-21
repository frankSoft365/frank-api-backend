package com.microsoft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.microsoft.model.dto.userPaymentAkSk.GenerateAkSkRequest;
import com.microsoft.model.entity.UserPaymentAkSk;
import com.microsoft.model.vo.GenerateAkSkVO;

public interface UserPaymentAkSkService extends IService<UserPaymentAkSk> {
    GenerateAkSkVO payAndGenerateAkSk(GenerateAkSkRequest request);

    void disableExpiredAkSk();

    UserPaymentAkSk getValidAkSk(Long userId);
}
