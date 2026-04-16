package com.microsoft.controller;

import com.microsoft.commen.Result;
import com.microsoft.utils.AvatarUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@Tag(name = "头像上传模块", description = "头像上传接口")
@Slf4j
@RestController
public class UploadController {
    @Resource
    private AvatarUtils avatarUtils;
    /**
     * 上传图片
     */
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile avatar) {
        // 校验
        avatarUtils.verifyAvatar(avatar);
        // 压缩 上传阿里云
        String url = avatarUtils.compressAndUploadAvatar(avatar);
        return Result.success(url);
    }
}
