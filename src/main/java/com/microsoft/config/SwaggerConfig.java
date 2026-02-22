package com.microsoft.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Spring Boot 3.5 + OpenAPI 配置类
 * @Profile 仅在 dev/test 环境启用，生产环境自动关闭
 */
@Configuration
@Profile({"dev", "test"}) // 生产环境（prod）禁用，避免暴露接口
public class SwaggerConfig {

    /**
     * 配置文档基本信息（标题、版本、描述）
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // 文档标题+描述+版本
                .info(new Info()
                        .title("项目API文档")
                        .description("Spring Boot 3.5 接口文档（支持在线调试）")
                        .version("1.0.0"));
    }
}