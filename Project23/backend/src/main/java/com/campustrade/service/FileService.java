package com.campustrade.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    String uploadFile(MultipartFile file);
    List<String> uploadFiles(MultipartFile[] files);
    boolean deleteFile(String url);
}
