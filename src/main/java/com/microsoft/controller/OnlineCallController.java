package com.microsoft.controller;

import com.microsoft.commen.ErrorCode;
import com.microsoft.commen.Result;
import com.microsoft.exception.BusinessException;
import com.microsoft.frankapisdk.client.FrankApiClient;
import com.microsoft.frankapisdk.commen.BaseApiResponse;
import com.microsoft.model.dto.onlineCall.OnlineCallRequest;
import com.microsoft.model.entity.InterfaceInfo;
import com.microsoft.service.InterfaceInfoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/onlineCall")
public class OnlineCallController {
    @Resource
    private FrankApiClient frankApiClient;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    /**
     * redis限流
     */
    @PostMapping
    public Result<BaseApiResponse> onlineCallInterface(@RequestBody OnlineCallRequest request) throws Exception {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "请求失败");
        }
        Long interfaceId = request.getId();
        if (interfaceId == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "请求失败");
        }
        // 查找数据库中是否存在该接口
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(interfaceId);
        // 不存在则抛异常
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "请求失败");
        }
        // 如果接口是关闭状态 拒绝访问
        if (Integer.valueOf(0).equals(interfaceInfo.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "接口已关闭，无法访问");
        }
        // 存在则根据url发送请求
        String fullUrl = interfaceInfo.getUrl();
        if (fullUrl == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "请求失败");
        }
        String url = "/api" + fullUrl.split("/api")[1];
        Object param = request.getParam();
        BaseApiResponse baseApiResponse = frankApiClient.callByUrl(interfaceId, url, param);
        return Result.success(baseApiResponse);
    }
}
