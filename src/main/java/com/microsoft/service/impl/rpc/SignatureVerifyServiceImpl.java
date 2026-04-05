package com.microsoft.service.impl.rpc;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.microsoft.frankapisdk.commen.ErrorCode;
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
                                                   Long interfaceId,
                                                   String signatureVersion,
                                                   String signatureMethod,
                                                   Long timestamp,
                                                   String businessData,
                                                   String clientSignature) {
        QueryWrapper<UserPaymentAkSk> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("access_key", accessKey);
        UserPaymentAkSk userPaymentAkSk = userPaymentAkSkService.getOne(queryWrapper);
        if (userPaymentAkSk == null) {
            return SignatureVerifyResponse.error(ErrorCode.InvalidParameter.getCode(), "AccessKey错误");
        }
        if (UserPaymentAkSkStatusEnum.STATUS_BANNED.getValue().equals(userPaymentAkSk.getAkskStatus())) {
            return SignatureVerifyResponse.error(ErrorCode.Forbidden.getCode(), "AccessKey已禁用或过期");
        }
        if (userPaymentAkSk.getServiceEndTime() != null && userPaymentAkSk.getServiceEndTime().isBefore(LocalDateTime.now())) {
            return SignatureVerifyResponse.error(ErrorCode.Forbidden.getCode(), "AccessKey已过服务期限");
        }
        // 验证接口 ID 存在性
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(interfaceId);
        if (interfaceInfo == null) {
            return SignatureVerifyResponse.error(ErrorCode.ActionUnavailable.getCode(), "接口不存在（ID: " + interfaceId + "）");
        }
        // 验证接口状态
        if (interfaceInfo.getStatus() == null || interfaceInfo.getStatus() == InterfaceInfoStatusEnum.OFFLINE.getValue()) {
            return SignatureVerifyResponse.error(ErrorCode.ActionUnavailable.getCode(), "当前接口处于停服维护状态，请稍后重试");
        }
        // 数据库中的请求路径
        String interfaceInfoPath = interfaceInfo.getUrl();
        if (!interfaceInfoPath.equals(requestPath) || !interfaceInfo.getMethod().equals(requestMethod)) {
            return SignatureVerifyResponse.error(ErrorCode.InvalidParameter.getCode(), "接口ID错误");
        }
        // 生成签名
        Map<String, String> params = new HashMap<>();
        params.put(SignatureRequestHeadersConstant.SIGNATURE_VERSION, signatureVersion);
        params.put(SignatureRequestHeadersConstant.SIGNATURE_METHOD, signatureMethod);
        params.put(SignatureRequestHeadersConstant.ACCESS_KEY_ID, accessKey);
        params.put(SignatureRequestHeadersConstant.TIMESTAMP, String.valueOf(timestamp));
        params.put(SignatureRequestHeadersConstant.INTERFACE_ID, String.valueOf(interfaceId));
        if (businessData != null) {
            params.put(SignatureRequestHeadersConstant.BUSINESS_DATA, businessData);
        }
        // 拿到数据库中的secretKey
        String secretKey = userPaymentAkSk.getSecretKeyHash();

        // 服务端根据请求参数和用户的隐蔽secretKey以同样方式生成签名
        String serverSignature = CloudSignUtils.generateSignByAllParams(secretKey, params, requestMethod, requestPath);
        // 校验签名
        if (!serverSignature.equals(clientSignature)) {
            return SignatureVerifyResponse.error(ErrorCode.SignatureFailure.getCode(), "身份认证失败，签名不匹配");
        }
        // 签名正确
        Long userId = userPaymentAkSk.getUserId();
        return SignatureVerifyResponse.success(userId);
    }
}
