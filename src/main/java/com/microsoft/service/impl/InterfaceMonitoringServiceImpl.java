package com.microsoft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.microsoft.commen.ErrorCode;
import com.microsoft.constant.CommonConstant;
import com.microsoft.constant.InterfaceMonitoringConstant;
import com.microsoft.exception.BusinessException;
import com.microsoft.mapper.InterfaceLogMapper;
import com.microsoft.mapper.UserInterfaceCallCountMapper;
import com.microsoft.model.dto.interfaceMonitoring.InterfaceStatQueryDTO;
import com.microsoft.model.dto.interfaceMonitoring.MonitorOverviewQueryDTO;
import com.microsoft.model.dto.interfaceMonitoring.UserCallRankQueryDTO;
import com.microsoft.model.dto.interfaceMonitoring.UserInterfaceLogQueryDTO;
import com.microsoft.model.entity.InterfaceLog;
import com.microsoft.model.enums.InterfaceLogSuccessEnum;
import com.microsoft.model.vo.interfaceMonitoring.*;
import com.microsoft.service.InterfaceMonitoringService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.microsoft.constant.ErrorDescriptionConstant.*;

@Service
public class InterfaceMonitoringServiceImpl implements InterfaceMonitoringService {

    private static final int TIME_QUERY_RANGE_LIMIT_DAY = 7;
    private static final int MAX_HISTORY_DAYS = 30;
    private static final int MAX_USER_CALL_RANK_COUNT = 1000;
    private static final String CALL_TREND_DAY_PATTERN = "MM-dd";
    private static final String CALL_TREND_HOUR_PATTERN = "HH:00";
    @Resource
    private InterfaceLogMapper interfaceLogMapper;
    @Resource
    private UserInterfaceCallCountMapper userInterfaceCallCountMapper;

    @Override
    public MonitorOverviewVO getAdminMonitorOverviewVO(MonitorOverviewQueryDTO queryDTO) {
        if (queryDTO == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, PARAM_EMPTY);
        }
        // 拿到筛选条件
        LocalDateTime startTime = queryDTO.getStartTime();
        LocalDateTime endTime = queryDTO.getEndTime();
        LocalDateTime queryTime = queryDTO.getQueryTime();
        // 校验
        long daysBetween = validateTimeQuery(startTime, endTime, queryTime);
        boolean exceedMaxHistoryDays = isExceedMaxHistoryDays(startTime, queryTime);
        if (exceedMaxHistoryDays) {
            return new MonitorOverviewVO(
                    0L,
                    0.0,
                    0.0,
                    new ArrayList<>()
            );
        }
        QueryWrapper<InterfaceLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("create_time", startTime);
        queryWrapper.le("create_time", endTime);
        List<InterfaceLog> logList = interfaceLogMapper.selectList(queryWrapper);
        long totalCall = logList.size();
        // 没有记录
        if (totalCall == 0) {
            return new MonitorOverviewVO(
                    totalCall,
                    0.0,
                    0.0,
                    new ArrayList<>()
            );
        }
        // 有记录
        // 获取成功率（保留1位小数）
        long successCount = logList.stream()
                .filter(log -> InterfaceLogSuccessEnum.SUCCESS.getValue().equals(log.getSuccess()))
                .count();
        double successRate = Math.round(successCount / ((double) totalCall) * 1000) / 10.0;
        // 获取平均耗时
        double avgCost = logList.stream()
                .mapToLong(InterfaceLog::getCostTime)
                .average()
                .orElse(0.0);
        // 获取调用趋势
        List<TimeValueVO> callTrend = generateCallTrend(logList, startTime, daysBetween);
        return new MonitorOverviewVO(
                totalCall,
                successRate,
                avgCost,
                callTrend
        );
    }

    @Override
    public IPage<InterfaceStatVO> getAdminInterfaceStatVOList(InterfaceStatQueryDTO queryDTO) {
        if (queryDTO == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, PARAM_EMPTY);
        }
        int current = queryDTO.getCurrent();
        int pageSize = queryDTO.getPageSize();
        String sortField = queryDTO.getSortField();
        String sortOrder = queryDTO.getSortOrder();
        LocalDateTime startTime = queryDTO.getStartTime();
        LocalDateTime endTime = queryDTO.getEndTime();
        LocalDateTime queryTime = queryDTO.getQueryTime();
        String interfacePath = queryDTO.getInterfacePath();
        String requestMethod = queryDTO.getRequestMethod();
        Integer requestResult = queryDTO.getRequestResult();

        validateTimeQuery(startTime, endTime, queryTime);
        boolean exceedMaxHistoryDays = isExceedMaxHistoryDays(startTime, queryTime);
        if (exceedMaxHistoryDays) {
            IPage<InterfaceStatVO> emptyPage = createEmptyPage();
            return emptyPage;
        }
        boolean isAsc = CommonConstant.SORT_ORDER_ASC.equals(sortOrder);
        if (StringUtils.isBlank(sortField)) {
            sortField = InterfaceMonitoringConstant.TOTAL;
        }
        if (!InterfaceMonitoringConstant.isValidSortField(sortField)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, QUERY_FIELD_INVALID);
        }
        // 根据条件分页查询
        // 获取查询条件
        Page<InterfaceStatVO> page = new Page<>(current, pageSize);
        QueryWrapper<InterfaceLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("create_time", startTime);
        queryWrapper.le("create_time", endTime);
        queryWrapper.groupBy("interface_url", "request_method");
        if (StringUtils.isNotBlank(interfacePath)) {
            queryWrapper.like("interface_url", interfacePath);
        }
        if (StringUtils.isNotBlank(requestMethod)) {
            queryWrapper.eq("request_method", requestMethod);
        }
        if (requestResult != null) {
            queryWrapper.eq("success", requestResult);
        }
        // 按调用量、成功率、平均耗时升序 / 降序
        queryWrapper.orderBy(true, isAsc, sortField);
        return interfaceLogMapper.selectInterfaceStatVOList(page, queryWrapper);
    }

    @Override
    public IPage<UserCallRankVO> getAdminUserCallRankVOList(UserCallRankQueryDTO queryDTO) {
        if (queryDTO == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, PARAM_EMPTY);
        }
        int current = queryDTO.getCurrent();
        int pageSize = queryDTO.getPageSize();
        LocalDateTime startTime = queryDTO.getStartTime();
        LocalDateTime endTime = queryDTO.getEndTime();
        LocalDateTime queryTime = queryDTO.getQueryTime();
        Long userId = queryDTO.getUserId();

        validateTimeQuery(startTime, endTime, queryTime);
        boolean exceedMaxHistoryDays = isExceedMaxHistoryDays(startTime, queryTime);
        if (exceedMaxHistoryDays) {
            IPage<UserCallRankVO> emptyPage = createEmptyPage();
            return emptyPage;
        }
        // 不能查到1000条
        if ((current - 1) * pageSize >= MAX_USER_CALL_RANK_COUNT) {
            return this.createEmptyPage();
        }

        Page<UserCallRankVO> page = new Page<>(current, pageSize);
        QueryWrapper<InterfaceLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("create_time", startTime);
        queryWrapper.le("create_time", endTime);
        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }
        queryWrapper.groupBy("user_id");
        queryWrapper.orderBy(true, false, "totalCall");
        return interfaceLogMapper.selectUserCallRankVOList(page, queryWrapper);
    }

    @Override
    public MonitorOverviewVO getUserMonitorOverviewVO(MonitorOverviewQueryDTO queryDTO, Long userId) {
        if (queryDTO == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, PARAM_EMPTY);
        }
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, PARAM_INVALID);
        }
        LocalDateTime startTime = queryDTO.getStartTime();
        LocalDateTime endTime = queryDTO.getEndTime();
        LocalDateTime queryTime = queryDTO.getQueryTime();
        long daysBetween = validateTimeQuery(startTime, endTime, queryTime);
        boolean exceedMaxHistoryDays = isExceedMaxHistoryDays(startTime, queryTime);
        if (exceedMaxHistoryDays) {
            return new MonitorOverviewVO(
                    0L,
                    0.0,
                    0.0,
                    new ArrayList<>()
            );
        }
        QueryWrapper<InterfaceLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("create_time", startTime);
        queryWrapper.le("create_time", endTime);
        queryWrapper.eq("user_id", userId);
        List<InterfaceLog> logList = interfaceLogMapper.selectList(queryWrapper);
        long totalCall = logList.size();
        // 没有记录
        if (totalCall == 0) {
            return new MonitorOverviewVO(
                    totalCall,
                    0.0,
                    0.0,
                    new ArrayList<>()
            );
        }
        // 有记录
        // 获取成功率（保留1位小数）
        long successCount = logList.stream()
                .filter(log -> InterfaceLogSuccessEnum.SUCCESS.getValue().equals(log.getSuccess()))
                .count();
        double successRate = Math.round(successCount / ((double) totalCall) * 1000) / 10.0;
        // 获取平均耗时
        double avgCost = logList.stream()
                .mapToLong(InterfaceLog::getCostTime)
                .average()
                .orElse(0.0);
        // 获取调用趋势
        List<TimeValueVO> callTrend = generateCallTrend(logList, startTime, daysBetween);
        return new MonitorOverviewVO(
                totalCall,
                successRate,
                avgCost,
                callTrend
        );
    }

    @Override
    public IPage<UserInterfaceLogVO> getUserInterfaceLogVOList(UserInterfaceLogQueryDTO queryDTO, Long userId) {
        if (queryDTO == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, PARAM_EMPTY);
        }
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, PARAM_INVALID);
        }
        int current = queryDTO.getCurrent();
        int pageSize = queryDTO.getPageSize();
        LocalDateTime startTime = queryDTO.getStartTime();
        LocalDateTime endTime = queryDTO.getEndTime();
        LocalDateTime queryTime = queryDTO.getQueryTime();
        String interfacePath = queryDTO.getInterfacePath();
        String requestMethod = queryDTO.getRequestMethod();
        Integer requestResult = queryDTO.getRequestResult();
        validateTimeQuery(startTime, endTime, queryTime);
        boolean exceedMaxHistoryDays = isExceedMaxHistoryDays(startTime, queryTime);
        if (exceedMaxHistoryDays) {
            IPage<UserInterfaceLogVO> emptyPage = createEmptyPage();
            return emptyPage;
        }
        Page<UserInterfaceLogVO> page = new Page<>(current, pageSize);
        QueryWrapper<InterfaceLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("create_time", startTime);
        queryWrapper.le("create_time", endTime);
        queryWrapper.eq("user_id", userId);
        if (StringUtils.isNotBlank(interfacePath)) {
            queryWrapper.like("interface_url", interfacePath);
        }
        if (StringUtils.isNotBlank(requestMethod)) {
            queryWrapper.eq("request_method", requestMethod);
        }
        if (requestResult != null) {
            queryWrapper.eq("success", requestResult);
        }
        queryWrapper.orderBy(true, false, "requestTime");
        return interfaceLogMapper.selectUserInterfaceLogVOList(page, queryWrapper);
    }

    @Override
    public UserCallDistributionVO getUserCallDistributionVO(Long userId) {
        if (userId == null || userId <= 0 ) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, PARAM_INVALID);
        }
        UserCallDistributionVO userCallDistributionVO = new UserCallDistributionVO();
        Integer totalSuccessCount = userInterfaceCallCountMapper.selectUserTotalCount(userId);
        userCallDistributionVO.setTotalSuccessCount(totalSuccessCount);
        List<InterfacePathCountVO> callDistribution = userInterfaceCallCountMapper.selectCallDistribution(userId);
        userCallDistributionVO.setCallDistribution(callDistribution);
        return userCallDistributionVO;
    }

    /**
     * 判断起始时间是否超过 最大限制查询历史时间
     * @param startTime 起始时间
     * @param queryTime 发起查询时间
     * @return true：超过了 false：合法没有超过
     */
    private boolean isExceedMaxHistoryDays(LocalDateTime startTime, LocalDateTime queryTime) {
        LocalDate earliestDate = queryTime.toLocalDate().minusDays(MAX_HISTORY_DAYS - 1);
        LocalDateTime earliestDateTime = LocalDateTime.of(earliestDate, LocalTime.MIN);
        return startTime.isBefore(earliestDateTime);
    }

    private <T> IPage<T> createEmptyPage() {
        Page<T> emptyPage = new Page<>(1, 10);
        emptyPage.setRecords(new ArrayList<>());
        emptyPage.setTotal(0);
        return emptyPage;
    }


    /**
     * 校验时间范围参数：空值、时间起始与结束合法、时间间隔合法并可选返回时间间隔
     * @param startTime 起始时间
     * @param endTime 结束时间
     * @param queryTime 发起查询的时间
     * @return 合法的时间间隔
     */
    private long validateTimeQuery(LocalDateTime startTime, LocalDateTime endTime, LocalDateTime queryTime) {
        if (startTime == null || endTime == null || queryTime == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, QUERY_TIME_EMPTY);
        }
        if (!startTime.isBefore(endTime)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, TIME_RANGE_INVALID);
        }
        if (endTime.isAfter(queryTime)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, TIME_EXCEEDS_CURRENT);
        }
        long daysBetween = ChronoUnit.DAYS.between(startTime, endTime);
        if (daysBetween > TIME_QUERY_RANGE_LIMIT_DAY) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, TIME_SPAN_EXCEEDS_LIMIT);
        }
        return daysBetween;
    }

    private List<TimeValueVO> generateCallTrend(List<InterfaceLog> logList, LocalDateTime startTime,
                                                long daysBetween) {
        List<TimeValueVO> trendList = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(CALL_TREND_DAY_PATTERN);
        DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern(CALL_TREND_HOUR_PATTERN);

        if (daysBetween < 1) {
            // 按小时分
            Map<Integer, Long> hourCountMap = logList.stream().collect(Collectors.groupingBy(
                    log -> log.getCreateTime().getHour(),
                    Collectors.counting()
            ));
            for (int hour = 0; hour < 24; hour++) {
                Long count = hourCountMap.getOrDefault(hour, 0L);
                trendList.add(new TimeValueVO(LocalTime.of(hour, 0).format(hourFormatter), count));
            }
        } else {
            // 按天分
            LocalDate startDate = startTime.toLocalDate();
            Map<LocalDate, Long> dateCountMap = logList.stream()
                    .collect(Collectors.groupingBy(
                            log -> log.getCreateTime().toLocalDate(),
                            Collectors.counting()
                    ));
            // 从前端要求的第一天开始拼接VO
            for (int i = 0; i < daysBetween + 1; i++) {
                LocalDate currentDate = startDate.plusDays(i);
                Long count = dateCountMap.getOrDefault(currentDate, 0L);
                // 将LocalDate类型的天数转化为固定格式的字符串
                trendList.add(new TimeValueVO(currentDate.format(dateFormatter), count));
            }
        }
        return trendList;
    }
}