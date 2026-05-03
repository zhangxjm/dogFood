package com.campustrade.service.impl;

import cn.hutool.core.util.IdUtil;
import com.campustrade.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.url-prefix}")
    private String urlPrefix;

    @Override
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = StringUtils.getFilenameExtension(originalFilename);
            
            if (!isImageExtension(extension)) {
                log.warn("不支持的文件格式: {}", extension);
                return null;
            }

            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String savePath = uploadPath + File.separator + datePath;
            
            File directory = new File(savePath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = IdUtil.simpleUUID() + "." + extension;
            File destFile = new File(savePath + File.separator + fileName);
            file.transferTo(destFile);

            String fileUrl = urlPrefix + datePath + "/" + fileName;
            log.info("文件上传成功: {}", fileUrl);
            
            return fileUrl;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return null;
        }
    }

    @Override
    public List<String> uploadFiles(MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return List.of();
        }

        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String url = uploadFile(file);
                if (url != null) {
                    urls.add(url);
                }
            }
        }
        return urls;
    }

    @Override
    public boolean deleteFile(String url) {
        if (!StringUtils.hasText(url)) {
            return false;
        }

        try {
            if (url.startsWith(urlPrefix)) {
                String relativePath = url.substring(urlPrefix.length());
                String filePath = uploadPath + File.separator + relativePath.replace("/", File.separator);
                
                File file = new File(filePath);
                if (file.exists()) {
                    boolean deleted = file.delete();
                    if (deleted) {
                        log.info("文件删除成功: {}", url);
                    }
                    return deleted;
                }
            }
            return false;
        } catch (Exception e) {
            log.error("文件删除失败", e);
            return false;
        }
    }

    private boolean isImageExtension(String extension) {
        if (extension == null) {
            return false;
        }
        String ext = extension.toLowerCase();
        return ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") || 
               ext.equals("gif") || ext.equals("webp") || ext.equals("bmp");
    }
}
