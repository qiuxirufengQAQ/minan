package com.minan.game.controller;

import com.minan.game.dto.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class UploadController {

    // 上传文件保存路径
    @Value("${upload.path}")
    private String uploadPath;

    /**
     * 上传文件
     */
    @PostMapping
    public Response<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("type") String type) {
        if (file.isEmpty()) {
            return Response.error("请选择要上传的文件");
        }

        try {
            // 确保上传目录存在
            File uploadDir = new File(uploadPath + File.separator + type);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + suffix;

            // 保存文件
            File dest = new File(uploadDir.getAbsolutePath() + File.separator + filename);
            file.transferTo(dest);

            // 返回文件访问路径
            String fileUrl = "/" + type + "/" + filename;
            return Response.success(fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return Response.error("文件上传失败：" + e.getMessage());
        }
    }
}
