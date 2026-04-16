package com.microsoft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.microsoft.commen.ErrorCode;
import com.microsoft.exception.BusinessException;
import com.microsoft.mapper.UserMapper;
import com.microsoft.mapper.UserPaymentAkSkMapper;
import com.microsoft.model.dto.userPaymentAkSk.GenerateAkSkRequest;
import com.microsoft.model.entity.User;
import com.microsoft.model.entity.UserPaymentAkSk;
import com.microsoft.model.enums.UserPaymentAkSkStatusEnum;
import com.microsoft.model.vo.GenerateAkSkVO;
import com.microsoft.service.UserPaymentAkSkService;
import com.microsoft.utils.AkSkGenerator;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

import static com.microsoft.constant.ErrorDescriptionConstant.*;

@Slf4j
@Service
class UserPaymentAkSkServiceImpl extends ServiceImpl<UserPaymentAkSkMapper, UserPaymentAkSk> implements UserPaymentAkSkService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserPaymentAkSkMapper paymentAkSkMapper;

    /**
     * 分配AK/SK
     */
    @Transactional(rollbackFor = Exception.class)
    public GenerateAkSkVO payAndGenerateAkSk(GenerateAkSkRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, PARAM_EMPTY);
        }
        Long userId = request.getUserId();
        Integer payDays = request.getPayDays();
        
        // 1. 校验用户存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, OBJECT_NOT_FOUND);
        }

        // 2. 查询/初始化用户的付费+AK/SK记录（未付费则初始化）
        LambdaQueryWrapper<UserPaymentAkSk> queryWrapper = new LambdaQueryWrapper<UserPaymentAkSk>()
                .eq(UserPaymentAkSk::getUserId, userId);
        UserPaymentAkSk paymentAkSk = paymentAkSkMapper.selectOne(queryWrapper);
        
        // 初始化未付费用户记录（AK/SK全NULL）
        if (paymentAkSk == null) {
            paymentAkSk = new UserPaymentAkSk();
            paymentAkSk.setUserId(userId);
            paymentAkSkMapper.insert(paymentAkSk);
            // 重新查询获取初始化的记录
            paymentAkSk = paymentAkSkMapper.selectOne(queryWrapper);
        }

        // 4. 生成AK/SK
        String ak = AkSkGenerator.generateAK(String.valueOf(userId));
        String sk = AkSkGenerator.generateSK();
        String[] skHashAndSalt = AkSkGenerator.hashSecretKey(sk);

        // 5. 设置付费服务期限
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime serviceStartTime = now;
        LocalDateTime serviceEndTime = now.plusDays(payDays);

        // 6. 更新合并表
        LambdaUpdateWrapper<UserPaymentAkSk> updateWrapper = new LambdaUpdateWrapper<UserPaymentAkSk>()
                .eq(UserPaymentAkSk::getUserId, userId)
                .set(UserPaymentAkSk::getServiceStartTime, serviceStartTime)
                .set(UserPaymentAkSk::getServiceEndTime, serviceEndTime)
                .set(UserPaymentAkSk::getAccessKey, ak)
                // 暂时以明文存储实现
                .set(UserPaymentAkSk::getSecretKeyHash, sk)
//                .set(UserPaymentAkSk::getSecretKeyHash, skHashAndSalt[0])
                .set(UserPaymentAkSk::getSecretKeySalt, skHashAndSalt[1])
                .set(UserPaymentAkSk::getAkskStatus, 1); // 启用AK/SK
        paymentAkSkMapper.update(null, updateWrapper);

        // 7. 返回结果
        GenerateAkSkVO response = new GenerateAkSkVO();
        response.setAccessKey(ak);
        response.setSecretKey(sk);
        response.setExpireTime(serviceEndTime);
        response.setUserId(userId);
        return response;
    }

    /**
     * 定时任务：禁用所有超期AK/SK
     */
    @Transactional(rollbackFor = Exception.class)
    public void disableExpiredAkSk() {
        LocalDateTime now = LocalDateTime.now();
        // 链式更新：付费已过期且AK/SK仍启用的记录
        LambdaUpdateWrapper<UserPaymentAkSk> updateWrapper = new LambdaUpdateWrapper<UserPaymentAkSk>()
                .le(UserPaymentAkSk::getServiceEndTime, now) // 服务结束时间≤当前时间
                .eq(UserPaymentAkSk::getAkskStatus, 1) // 状态为启用
                .set(UserPaymentAkSk::getAkskStatus, 0); // 改为禁用
        paymentAkSkMapper.update(null, updateWrapper);
    }

    /**
     * 获取用户有效的 AK/SK
     * @param userId 用户 ID
     * @return 有效的 AK/SK记录
     * @throws BusinessException 当用户未购买、AK/SK已禁用或已过期时抛出异常
     */
    @Override
    public UserPaymentAkSk getValidAkSk(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, PARAM_INVALID);
        }

        // 查询用户的 AK/SK记录
        LambdaQueryWrapper<UserPaymentAkSk> queryWrapper = new LambdaQueryWrapper<UserPaymentAkSk>()
                .eq(UserPaymentAkSk::getUserId, userId);
        UserPaymentAkSk paymentAkSk = paymentAkSkMapper.selectOne(queryWrapper);

        // 校验是否存在记录
        if (paymentAkSk == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, OBJECT_NOT_FOUND);
        }

        // 校验 AK/SK 状态
        if (UserPaymentAkSkStatusEnum.STATUS_BANNED.getValue().equals(paymentAkSk.getAkskStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, AK_SK_BANNED);
        }

        // 校验是否过期
        LocalDateTime now = LocalDateTime.now();
        if (paymentAkSk.getServiceEndTime() != null && paymentAkSk.getServiceEndTime().isBefore(now)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, AK_SK_EXPIRED);
        }
        return paymentAkSk;
    }
}