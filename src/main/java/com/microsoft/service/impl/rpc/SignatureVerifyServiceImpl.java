package com.microsoft.service.impl.rpc;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.microsoft.frankapisdk.commen.ErrorCode;
import com.microsoft.frankapisdk.constant.ErrorMessageConstant;
import com.microsoft.frankapisdk.constant.SignatureRequestHeadersConstant;
import com.microsoft.frankapisdk.service.SignatureVerifyService;
import com.microsoft.frankapisdk.service.model.dto.SignatureVerifyResponse;
import com.microsoft.frankapisdk.utils.CloudSignUtils;
import com.microsoft.model.entity.InterfaceInfo;
import com.microsoft.model.entity.UserPaymentAkSk;
import com.microsoft.model.enums.InterfaceInfoStatusEnum;
import com.microsoft.model.enums.UserPaymentAkSkStatusEnum;
import com.microsoft.service.InterfaceInfoService;
import com.microsoft.service.UserPaymentAkSkService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@DubboService
public class SignatureVerifyServiceImpl implements SignatureVerifyService {
    @Resource
    private UserPaymentAkSkService userPaymentAkSkService;
    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Override
    public SignatureVerifyResponse verifySignature(String accessKey,
                                                   String requestMethod,
                                                   String requestPath,
                                                   String signatureVersion,
                                                   String signatureMethod,
                                                   Long timestamp,
                                                   String businessData,
                                                   String clientSignature) {
        QueryWrapper<UserPaymentAkSk> userPaymentAkSkQueryWrapper = new QueryWrapper<>();
        userPaymentAkSkQueryWrapper.eq("access_key", accessKey);
        UserPaymentAkSk userPaymentAkSk = userPaymentAkSkService.getOne(userPaymentAkSkQueryWrapper);
        if (userPaymentAkSk == null) {
            return SignatureVerifyResponse.error(ErrorCode.InvalidParameter.getCode(), ErrorMessageConstant.InvalidParameter_001);
        }
        if (UserPaymentAkSkStatusEnum.STATUS_BANNED.getValue().equals(userPaymentAkSk.getAkskStatus())) {
            return SignatureVerifyResponse.error(ErrorCode.Forbidden.getCode(), ErrorMessageConstant.Forbidden_001);
        }
        if (userPaymentAkSk.getServiceEndTime() != null && userPaymentAkSk.getServiceEndTime().isBefore(LocalDateTime.now())) {
            return SignatureVerifyResponse.error(ErrorCode.Forbidden.getCode(), ErrorMessageConstant.Forbidden_002);
        }
        // 验证接口存在性
        QueryWrapper<InterfaceInfo> interfaceInfoQueryWrapper = new QueryWrapper<>();
        interfaceInfoQueryWrapper.eq("url", requestPath);
        interfaceInfoQueryWrapper.eq("method", requestMethod);
        InterfaceInfo interfaceInfo = interfaceInfoService.getOne(interfaceInfoQueryWrapper);
        if (interfaceInfo == null) {
            return SignatureVerifyResponse.error(ErrorCode.ActionUnavailable.getCode(), ErrorMessageConstant.ActionUnavailable_001);
        }
        // 验证接口状态
        if (interfaceInfo.getStatus() == null || interfaceInfo.getStatus() == InterfaceInfoStatusEnum.OFFLINE.getValue()) {
            return SignatureVerifyResponse.error(ErrorCode.ActionUnavailable.getCode(), ErrorMessageConstant.ActionUnavailable_002);
        }
        // 生成签名
        Map<String, String> params = new HashMap<>();
        params.put(SignatureRequestHeadersConstant.SIGNATURE_VERSION, signatureVersion);
        params.put(SignatureRequestHeadersConstant.SIGNATURE_METHOD, signatureMethod);
        params.put(SignatureRequestHeadersConstant.ACCESS_KEY_ID, accessKey);
        params.put(SignatureRequestHeadersConstant.TIMESTAMP, String.valueOf(timestamp));
        if (businessData != null) {
            params.put(SignatureRequestHeadersConstant.BUSINESS_DATA, businessData);
        }
        // 拿到数据库中的secretKey
        String secretKey = userPaymentAkSk.getSecretKeyHash();

        // 服务端根据请求参数和用户的隐蔽secretKey以同样方式生成签名
        String serverSignature = CloudSignUtils.generateSignByAllParams(secretKey, params, requestMethod, requestPath);
        // 校验签名
        if (!serverSignature.equals(clientSignature)) {
            return SignatureVerifyResponse.error(ErrorCode.SignatureFailure.getCode(), ErrorMessageConstant.SignatureFailure_001);
        }
        // 签名正确
        Long userId = userPaymentAkSk.getUserId();
        Long interfaceInfoId = interfaceInfo.getId();
        return SignatureVerifyResponse.success(userId, interfaceInfoId);
    }
}
