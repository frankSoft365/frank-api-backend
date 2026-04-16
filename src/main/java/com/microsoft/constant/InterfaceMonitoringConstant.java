package com.microsoft.constant;

import java.util.Set;

public class InterfaceMonitoringConstant {
    /**
     * total 调用量
     */
    public static final String TOTAL = "total";

    /**
     * successRate 成功率
     */
    public static final String SUCCESS_RATE = "successRate";
    /**
     * avgCost 耗时
     */
    public static final String AVG_COST = "avgCost";

    /**
     * 允许的排序字段集合
     */
    public static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            TOTAL,
            SUCCESS_RATE,
            AVG_COST
    );

    /**
     * 校验排序字段是否合法
     *
     * @param sortField 排序字段
     * @return true-合法，false-不合法
     */
    public static boolean isValidSortField(String sortField) {
        return ALLOWED_SORT_FIELDS.contains(sortField);
    }
}
