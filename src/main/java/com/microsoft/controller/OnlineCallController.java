package com.microsoft.controller;

import com.microsoft.commen.Result;
import com.microsoft.frankapisdk.client.FrankApiClient;
import com.microsoft.frankapisdk.client.FrankApiClientConfig;
import com.microsoft.frankapisdk.commen.BaseApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/onlineCall")
public class OnlineCallController {

//    @PostMapping
//    public Result<BaseApiResponse> onlineCallInterface() throws Exception {
//
//        BaseApiResponse baseApiResponse = frankApiClient.callSimpleHello(2025807209132167169L);
//        return Result.success(baseApiResponse);
//    }
}
