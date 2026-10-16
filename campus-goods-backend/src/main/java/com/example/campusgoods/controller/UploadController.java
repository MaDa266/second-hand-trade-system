package com.example.campusgoods.controller;

import com.example.campusgoods.vo.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 文件上传接口：商品图片上传到本地 uploads 目录
 */
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    /** 上传目录：项目运行目录下的 uploads/ */
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + File.separator + "uploads";

    /** 允许的图片后缀 */
    private static final Set<String> ALLOW_SUFFIX =
            new HashSet<>(Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp"));

    @PostMapping
    public Result<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return Result.error("文件不能为空");
        }
        String original = file.getOriginalFilename();
        if (original == null || !original.contains(".")) {
            return Result.error("文件格式不正确");
        }
        String suffix = original.substring(original.lastIndexOf(".")).toLowerCase();
        if (!ALLOW_SUFFIX.contains(suffix)) {
            return Result.error("仅支持 jpg/jpeg/png/gif/webp 图片");
        }
        String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;

        File dir = new File(UPLOAD_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            return Result.error("上传目录创建失败");
        }
        file.transferTo(new File(dir, fileName));

        return Result.success("/uploads/" + fileName);
    }
}
