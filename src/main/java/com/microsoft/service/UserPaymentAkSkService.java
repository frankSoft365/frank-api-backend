package com.microsoft.service;

import com.microsoft.model.dto.userPaymentAkSk.GenerateAkSkRequest;
import com.microsoft.model.entity.UserPaymentAkSk;
import com.microsoft.model.vo.GenerateAkSkVO;

public interface UserPaymentAkSkService {
    GenerateAkSkVO payAndGenerateAkSk(GenerateAkSkRequest request);

    void disableExpiredAkSk();

    UserPaymentAkSk getValidAkSk(Long userId);
}
