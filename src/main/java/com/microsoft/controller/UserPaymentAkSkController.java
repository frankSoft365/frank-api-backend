package com.microsoft.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.microsoft.commen.ErrorCode;
import com.microsoft.commen.Result;
import com.microsoft.constant.ErrorDescriptionConstant;
import com.microsoft.exception.BusinessException;
import com.microsoft.model.entity.UserPaymentAkSk;
import com.microsoft.model.enums.UserPaymentAkSkStatusEnum;
import com.microsoft.model.vo.CheckViewChanceVO;
import com.microsoft.model.vo.ViewAkSkVO;
import com.microsoft.service.UserPaymentAkSkService;
import com.microsoft.utils.CurrentHold;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import static com.microsoft.constant.ErrorDescriptionConstant.*;


@RestController
@RequestMapping("/userPaymentAkSk")
public class UserPaymentAkSkController {
    @Resource
    private UserPaymentAkSkService userPaymentAkSkService;

    /**
     * 用户有且仅有一次的查询ak,sk机会
     */
    @GetMapping("/viewAkSk")
    public Result<ViewAkSkVO> viewAkSk() {
        Long userId = CurrentHold.getCurrentId();
        UserPaymentAkSk userPaymentAkSk = userPaymentAkSkService.getValidAkSk(userId);
        if (userPaymentAkSk == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, OBJECT_NOT_FOUND);
        }
        if (UserPaymentAkSkStatusEnum.CANNOT_VIEW.getValue().equals(userPaymentAkSk.getAkskViewStatus())) {
            throw new BusinessException(ErrorCode.NO_AUTH, VIEW_ONCE_ONLY);
        }
        String accessKey = userPaymentAkSk.getAccessKey();
        String secretKey = userPaymentAkSk.getSecretKeyHash();
        if (accessKey == null || secretKey == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, ErrorDescriptionConstant.SYSTEM_INTERNAL_ERROR);
        }
        LambdaUpdateWrapper<UserPaymentAkSk> updateWrapper = new LambdaUpdateWrapper<UserPaymentAkSk>()
                .eq(UserPaymentAkSk::getId, userPaymentAkSk.getId())
                .set(UserPaymentAkSk::getAkskViewStatus, UserPaymentAkSkStatusEnum.CANNOT_VIEW.getValue());
        userPaymentAkSkService.update(updateWrapper);
        return Result.success(new ViewAkSkVO(accessKey, secretKey));
    }

    /**
     * 仅浅查询用户是否有查看aksk的机会
     */
    @GetMapping("/checkViewChance")
    public Result<CheckViewChanceVO> checkViewChance() {
        Long userId = CurrentHold.getCurrentId();
        // 查询用户的 AK/SK记录
        QueryWrapper<UserPaymentAkSk> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        UserPaymentAkSk userPaymentAkSk = userPaymentAkSkService.getOne(queryWrapper);
        // 如果不存在记录
        if (userPaymentAkSk == null) {
            return Result.success(new CheckViewChanceVO(false, "请先注册"));
        }
        // 检查 AK/SK状态
        if (userPaymentAkSk.getAkskStatus() != 1) {
            return Result.success(new CheckViewChanceVO(false, "密钥已被禁用"));
        }
        // 检查是否已查看过
        if (UserPaymentAkSkStatusEnum.CANNOT_VIEW.getValue().equals(userPaymentAkSk.getAkskViewStatus())) {
            return Result.success(new CheckViewChanceVO(false, "您已使用过查看机会，无法再次查看"));
        }
        // 可以查看
        return Result.success(new CheckViewChanceVO(true, "您有查看密钥的机会"));
    }

}