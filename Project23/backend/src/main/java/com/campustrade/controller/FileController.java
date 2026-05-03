package com.campustrade.controller;

import com.campustrade.common.Result;
import com.campustrade.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public Result<String> uploadFile(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        if (file == null || file.isEmpty()) {
            return Result.error("文件不能为空");
        }
        
        String url = fileService.uploadFile(file);
        if (url != null) {
            return Result.success("上传成功", url);
        }
        return Result.error("上传失败");
    }

    @PostMapping("/upload-multiple")
    public Result<List<String>> uploadMultipleFiles(HttpServletRequest request, 
                                                      @RequestParam("files") MultipartFile[] files) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        if (files == null || files.length == 0) {
            return Result.error("文件不能为空");
        }
        
        List<String> urls = fileService.uploadFiles(files);
        return Result.success("上传成功", urls);
    }

    @PostMapping("/delete")
    public Result<Void> deleteFile(HttpServletRequest request, @RequestBody Map<String, String> params) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        
        String url = params.get("url");
        if (url == null) {
            return Result.error("文件URL不能为空");
        }
        
        boolean success = fileService.deleteFile(url);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }
}
