package com.microsoft.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.microsoft.annotation.AuthCheck;
import com.microsoft.commen.ErrorCode;
import com.microsoft.commen.Result;
import com.microsoft.exception.BusinessException;
import com.microsoft.model.dto.user.UserUpdateRequest;
import com.microsoft.model.entity.User;
import com.microsoft.model.dto.user.UserLoginRequest;
import com.microsoft.model.dto.user.UserRegisterRequest;
import com.microsoft.model.vo.UserImportVO;
import com.microsoft.model.vo.UserLoginVO;
import com.microsoft.model.vo.UserVO;
import com.microsoft.service.UserService;
import com.microsoft.utils.CurrentHold;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.microsoft.constant.ErrorDescriptionConstant.*;
import static com.microsoft.constant.UserConstant.ADMIN_ROLE;

@Tag(name = "用户模块", description = "用户的增删改查接口")
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            // 参数为空错误
            throw new BusinessException(ErrorCode.PARAM_ERROR, PARAM_EMPTY);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String password = userRegisterRequest.getPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAllBlank(userAccount, password, checkPassword)) {
            // 参数为空串
            throw new BusinessException(ErrorCode.PARAM_ERROR, CREDENTIAL_INCOMPLETE);
        }
        Long userId = userService.userRegister(userAccount, password, checkPassword);
        return Result.success(userId);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<UserLoginVO> userLogin(@RequestBody UserLoginRequest userLoginRequest) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, PARAM_EMPTY);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String password = userLoginRequest.getPassword();
        if (StringUtils.isAllBlank(userAccount, password)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, LOGIN_CREDENTIAL_EMPTY);
        }
        UserLoginVO userLoginVO = userService.userLogin(userAccount, password);
        return Result.success(userLoginVO);
    }

    /**
     * 用户编辑个人信息 只能编辑 用户名 性别 电话 头像 邮箱
     */
    @PutMapping("/update")
    public Result<Void> updateUserInfo(@RequestBody UserUpdateRequest userInfoToUpdate) {
        if (userInfoToUpdate == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, PARAM_EMPTY);
        }
        Long currentId = CurrentHold.getCurrentId();
        User user = new User();
        BeanUtils.copyProperties(userInfoToUpdate, user);
        user.setId(currentId);
        boolean update = userService.updateById(user);
        if (!update) {
            throw new BusinessException(ErrorCode.DATABASE_ERROR, DATABASE_UPDATE_FAILED);
        }
        return Result.success();
    }

    /**
     * 根据用户名模糊查询用户列表
     */
    @AuthCheck(mustRole = ADMIN_ROLE)
    @GetMapping("/search")
    public Result<List<UserVO>> searchUsers(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isAllBlank(username)) {
            queryWrapper.like("username", username);
        }
        // 用户信息脱敏
        List<User> list = userService.list(queryWrapper);
        List<UserVO> userVOList = userService.getUserVO(list);
        return Result.success(userVOList);
    }

    /**
     * 管理员批量上传用户 并校验用户信息
     */
    @PostMapping("/batchImportUser")
    @AuthCheck(mustRole = ADMIN_ROLE)
    public Result<UserImportVO> batchImportUser(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, FILE_EMPTY);
        }
        UserImportVO userImportVO = userService.verifyAndBatchImportUser(file);
        return Result.success(userImportVO);
    }

    /**
     * 根据用户的登录态获取用户信息
     */
    @GetMapping("/current")
    public Result<UserVO> getCurrentUser() {
        Long currentId = CurrentHold.getCurrentId();
        User user = userService.getById(currentId);
        UserVO userVO = userService.getUserVO(user);
        return Result.success(userVO);
    }

    /**
     * 删除用户 只有管理员可以发起删除请求
     */
    @AuthCheck(mustRole = ADMIN_ROLE)
    @PostMapping("/delete")
    public Result<Void> deleteUser(@RequestBody Long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, PARAM_FORMAT_ERROR);
        }
        userService.removeById(id);
        return Result.success();
    }
}
