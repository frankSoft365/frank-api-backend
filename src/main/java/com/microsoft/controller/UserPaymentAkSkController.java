package com.microsoft.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.microsoft.commen.ErrorCode;
import com.microsoft.commen.Result;
import com.microsoft.exception.BusinessException;
import com.microsoft.model.entity.UserPaymentAkSk;
import com.microsoft.model.enums.UserPaymentAkSkStatusEnum;
import com.microsoft.model.vo.CheckViewChanceVO;
import com.microsoft.model.vo.ViewAkSkVO;
import com.microsoft.service.UserPaymentAkSkService;
import com.microsoft.utils.CurrentHold;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;


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
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户未登录");
        }
        UserPaymentAkSk userPaymentAkSk = userPaymentAkSkService.getValidAkSk(userId);
        if (userPaymentAkSk == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户信息不存在");
        }
        if (UserPaymentAkSkStatusEnum.CANNOT_VIEW.getValue().equals(userPaymentAkSk.getAkskViewStatus())) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无法再次查看");
        }
        String accessKey = userPaymentAkSk.getAccessKey();
        String secretKey = userPaymentAkSk.getSecretKeyHash();
        if (accessKey == null || secretKey == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "AK/SK 信息不完整");
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
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户未登录");
        }

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
            return Result.success(new CheckViewChanceVO(false, "AK/SK已被禁用"));
        }

        // 检查是否已查看过
        if (UserPaymentAkSkStatusEnum.CANNOT_VIEW.getValue().equals(userPaymentAkSk.getAkskViewStatus())) {
            return Result.success(new CheckViewChanceVO(false, "您已使用过查看机会，无法再次查看"));
        }

        // 可以查看
        return Result.success(new CheckViewChanceVO(true, "您有查看 AK/SK 的机会"));
    }

}