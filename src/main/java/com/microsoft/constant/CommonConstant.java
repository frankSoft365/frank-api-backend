package com.microsoft.constant;

/**
 * 通用常量
 */
public interface CommonConstant {

    /**
     * 升序
     */
    String SORT_ORDER_ASC = "ascend";

    /**
     * 降序
     */
    String SORT_ORDER_DESC = "descend";
    // token 的请求头键
    String TOKEN_REQUEST_HEADER_KEY = "Authorization";
    // token 值 'Bearer kdasfhasjfksla...' 以 Bearer 开头
    String TOKEN_START_WITH = "Bearer ";
    // token payload 键：id
    String TOKEN_PAYLOAD_KEY_1 = "id";
    // token payload 键：userAccount
    String TOKEN_PAYLOAD_KEY_2 = "userAccount";
    // 接口路径以 '/' 开头
    String API_PATH_START_WITH = "/";
}
