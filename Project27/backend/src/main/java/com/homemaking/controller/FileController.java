package com.homemaking.controller;

import com.homemaking.common.Result;
import com.homemaking.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class FileController {
    
    @Autowired
    private FileService fileService;
    
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        return fileService.upload(file);
    }
}
