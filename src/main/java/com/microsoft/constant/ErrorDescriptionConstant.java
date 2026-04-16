package com.microsoft.constant;

public class ErrorDescriptionConstant {
    public static final String SYSTEM_INTERNAL_ERROR = "系统内部错误";
    public static final String INTERFACE_CONNECTION_ERROR = "接口连接异常，无法发布";
    public static final String INTERFACE_UNAVAILABLE = "接口状态是关闭或暂时维护，无法访问";
    public static final String NO_PERMISSION = "用户无权限访问";
    public static final String VIEW_ONCE_ONLY = "查看机会仅有一次，无法再次查看";

    public static final String PARAM_EMPTY = "请求参数为空";
    public static final String PARAM_INVALID = "请求参数为空或不合法";
    public static final String PARAM_FORMAT_ERROR = "请求参数不合法";
    public static final String PAGE_SIZE_EXCEEDED = "不支持查询过大单页数据条数";
    public static final String CREDENTIAL_INCOMPLETE = "账户名或密码或确认码为空";
    public static final String LOGIN_CREDENTIAL_EMPTY = "账户名或密码为空";
    public static final String USER_ACCOUNT_LENGTH_INVALID = "账户名长度不合法";
    public static final String PASSWORD_LENGTH_INVALID = "密码长度不合法";
    public static final String USER_ACCOUNT_FORMAT_INVALID = "账户名包含字符不合法";
    public static final String PASSWORD_FORMAT_INVALID = "密码包含字符不合法";
    public static final String PASSWORD_AND_CODE_DO_NOT_MATCH = "密码与确认码不一致";
    public static final String USER_ACCOUNT_DUPLICATE = "账户名已存在";
    public static final String USER_ACCOUNT_OR_PASSWORD_INCORRECT = "用户名或密码错误";
    public static final String FILE_EMPTY = "所要上传的文件为空";
    public static final String FILE_SIZE_EXCEEDED = "上传文件大小超出限度";
    public static final String INTERFACE_REQUIRED_FIELDS_MISSING = "接口名称、地址、请求方法是必填项";
    public static final String INTERFACE_PATH_INVALID = "接口路径应以“/”开头";
    public static final String REQUEST_HEADER_TOO_LONG = "请求头过长";
    public static final String RESPONSE_HEADER_TOO_LONG = "响应头过长";
    public static final String REQUEST_PARAM_TOO_LONG = "请求参数过长";
    public static final String INTERFACE_DESC_TOO_LONG = "接口描述过长";
    public static final String QUERY_TIME_EMPTY = "查询时间参数不能为空";
    public static final String TIME_RANGE_INVALID = "开始时间不能晚于结束时间";
    public static final String TIME_EXCEEDS_CURRENT = "结束时间不能超过当前时间";
    public static final String TIME_SPAN_EXCEEDS_LIMIT = "时间跨度不能超过7天";
    public static final String TIME_EXCEEDS_MAX_HISTORY_DAYS = "只能查询最近30天内的数据";
    public static final String AK_SK_BANNED = "密钥已禁用，无法调用接口";
    public static final String AK_SK_EXPIRED = "服务已过期，请续费";

    public static final String INTERFACE_PATH_DUPLICATE = "接口请求路径已存在，不允许重复添加或修改";
    public static final String INTERFACE_STATUS_INVALID = "接口状态值不符合规定值";
    public static final String QUERY_FIELD_INVALID = "查询字段不符合规定值";
    public static final String OBJECT_NOT_FOUND = "找不到操作对象";

    public static final String DATABASE_DELETE_FAILED = "删除失败";
    public static final String DATABASE_INSERT_FAILED = "添加失败";
    public static final String DATABASE_UPDATE_FAILED = "修改失败";

}
