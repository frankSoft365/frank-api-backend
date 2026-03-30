package com.microsoft.controller;

import com.microsoft.commen.ErrorCode;
import com.microsoft.commen.Result;
import com.microsoft.config.FrankApiGatewayConfig;
import com.microsoft.exception.BusinessException;
import com.microsoft.frankapisdk.client.FrankApiClient;
import com.microsoft.frankapisdk.commen.BaseApiResponse;
import com.microsoft.model.dto.onlineCall.OnlineCallRequest;
import com.microsoft.model.entity.InterfaceInfo;
import com.microsoft.model.entity.UserPaymentAkSk;
import com.microsoft.model.enums.InterfaceInfoStatusEnum;
import com.microsoft.service.InterfaceInfoService;
import com.microsoft.service.UserPaymentAkSkService;
import com.microsoft.utils.CurrentHold;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/onlineCall")
public class OnlineCallController {
    @Resource
    private InterfaceInfoService interfaceInfoService;
    @Resource
    private UserPaymentAkSkService userPaymentAkSkService;

    // 网关的地址
    @Resource
    private FrankApiGatewayConfig frankApiGatewayConfig;

    /**
     * redis限流
     */
    @PostMapping
    public Result<BaseApiResponse> onlineCallInterface(@RequestBody OnlineCallRequest request) throws Exception {
        if (request == null || request.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "请求失败");
        }
        Long interfaceId = request.getId();
        // 查找数据库中是否存在该接口
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(interfaceId);
        // 不存在则抛异常
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "请求失败");
        }
        // 如果接口是关闭状态 拒绝访问
        if (!Integer.valueOf(InterfaceInfoStatusEnum.RELEASE.getValue()).equals(interfaceInfo.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "接口已关闭，无法访问");
        }
        // 存在则根据url发送请求
        String fullUrl = interfaceInfo.getUrl();
        if (fullUrl == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "请求失败");
        }
        String url = "/api" + fullUrl.split("/api")[1];
        Object param = request.getParam();
        // 查询到用户的ak,sk，用该ak,sk调用接口
        Long userId = CurrentHold.getCurrentId();
        // 调用 Service 层方法获取有效的 AK/SK
        UserPaymentAkSk paymentAkSk = userPaymentAkSkService.getValidAkSk(userId);

        // 使用用户的 AK/SK 调用接口
        String baseUrl = frankApiGatewayConfig.getBaseUrl();
        FrankApiClient frankApiClient = new FrankApiClient(baseUrl,
                paymentAkSk.getAccessKey(),
                paymentAkSk.getSecretKeyHash());
        BaseApiResponse baseApiResponse = frankApiClient.callByUrl(interfaceId, url, param);
        return Result.success(baseApiResponse);
    }
}
