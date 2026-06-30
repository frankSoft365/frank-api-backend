package com.microsoft.controller;

import com.microsoft.commen.ErrorCode;
import com.microsoft.commen.Result;
import com.microsoft.exception.BusinessException;
import com.microsoft.frankapisdk.client.FrankApiClient;
import com.microsoft.frankapisdk.commen.ApiResponse;
import com.microsoft.frankapisdk.commen.ErrorResponse;
import com.microsoft.frankapisdk.model.SendMailRequest;
import com.microsoft.frankapisdk.model.response.SendMailResponse;
import com.microsoft.model.dto.email.SendVerificationCodeRequest;
import com.microsoft.utils.RegexUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

import static com.microsoft.constant.ErrorDescriptionConstant.*;

@Tag(name = "邮箱验证码模块", description = "邮箱验证码发送与校验相关接口")
@Slf4j
@RestController
@RequestMapping("/email/verification-code")
public class EmailVerificationCodeController {

    private static final String BUSINESS_NAME = "frank-api";
    private static final String EMAIL_SUBJECT = "frank-api-email-verification-code";
    private static final String REDIS_KEY_PREFIX = "email:verifyCode:";
    private static final long VERIFY_CODE_EXPIRE_MINUTES = 5;

    @Resource
    private FrankApiClient frankApiClient;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 发送邮箱验证码
     */
    @Operation(summary = "发送邮箱验证码", description = "向指定邮箱发送 6 位数字验证码，用于邮箱校验，验证码有效期 5 分钟")
    @PostMapping("/send")
    public Result<Void> sendVerificationCode(@RequestBody SendVerificationCodeRequest request) {
        // 1. 参数校验
        if (request == null || StringUtils.isBlank(request.getEmail())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, EMAIL_EMPTY);
        }
        String email = request.getEmail().trim();
        if (!RegexUtils.isValidEmail(email)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, EMAIL_FORMAT_INVALID);
        }

        // 2. 组装 SDK 请求参数
        SendMailRequest sendMailRequest = new SendMailRequest(email, BUSINESS_NAME, EMAIL_SUBJECT);

        // 3. 调用 frankapi-client 发送邮件
        ApiResponse apiResponse;
        try {
            apiResponse = frankApiClient.callSendMailResponse(sendMailRequest);
        } catch (RuntimeException e) {
            log.error("调用 frankapi-client 发送邮箱验证码失败，email: {}", email, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, EMAIL_SEND_FAILED);
        }

        // 4. 处理错误响应
        if (apiResponse instanceof ErrorResponse) {
            log.warn("frankapi-client 返回错误响应，email: {}, response: {}", email, apiResponse);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, EMAIL_SEND_FAILED);
        }

        // 5. 提取验证码并存储到 Redis
        String verifyCode = null;
        if (apiResponse instanceof SendMailResponse) {
            verifyCode = ((SendMailResponse) apiResponse).getVerifyCode();
        }

        if (StringUtils.isBlank(verifyCode)) {
            log.warn("frankapi-client 返回的验证码为空，email: {}", email);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, EMAIL_SEND_FAILED);
        }

        String redisKey = REDIS_KEY_PREFIX + email;
        redisTemplate.opsForValue().set(redisKey, verifyCode, VERIFY_CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);

        log.info("邮箱验证码发送成功，email: {}, requestId: {}, verifyCode: {}", email, apiResponse.getRequestId(), verifyCode);
        return Result.success();
    }
}
