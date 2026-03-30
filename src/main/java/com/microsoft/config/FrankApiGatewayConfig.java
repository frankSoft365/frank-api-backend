package com.microsoft.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FrankApiGatewayConfig {
    @Value("${frank.api.gateway.baseUrl}")
    private String baseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }
}
