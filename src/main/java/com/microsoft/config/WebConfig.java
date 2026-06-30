package com.microsoft.config;

import com.microsoft.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private TokenInterceptor tokenInterceptor;

    // 配置跨域请求 允许所有来源的请求
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    // 配置请求拦截器 拦截所有的向后端的请求
    // 请求头中的字段值：token 尝试解析token
    // 解析失败（token无效 token过期 token为空）抛出业务异常无权限用户未登录
    // 解析成功 保存token中的用户信息在线程存储中 放行该请求
    // 配置拦截器不拦截的请求路径 登录 注册
    // 将controller层的登录态判断代码删除
    // 在登录成功后 生成token 返回给前端
    // 生成token的工具类 解析token 生成token 两个方法
    // 前端补充 请求拦截器 如果浏览器存储有token 就把token添加到请求头中发给后端
    // 登录后将后端传过来的token存到浏览器存储中
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/login",
                        "/user/register",
                        "/email/verification-code/send",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/v3/api-docs.yaml",
                        "/webjars/**");
    }
}
