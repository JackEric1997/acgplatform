package com.acg.minio.service.Impl;

import com.acg.minio.config.MinioConfig;
import com.acg.minio.service.MinioService;
import com.acg.minio.util.MinIoUtil;
import com.acg.common.utils.R;
import com.acg.common.entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
public class MinioServiceImpl implements MinioService {

    @Autowired
    private MinIoUtil minIoUtil;

    @Autowired
    private MinioConfig minioConfig;

    @Override
    public R upload(MultipartFile file) {
        if (file.isEmpty() || file.getSize() == 0){
            return new R(false,StatusCode.ERROR,"文件为空");
        }
        try {
            String fileName = file.getOriginalFilename();
            String newName = UUID.randomUUID().toString().replace("-","") + fileName.substring(fileName.lastIndexOf("."));
            InputStream inputStream = file.getInputStream();
            minIoUtil.putObject(minioConfig.getBucketName(),newName,inputStream,file.getContentType());
            inputStream.close();
            String url = minIoUtil.presignedGetObject(minioConfig.getBucketName(),newName);
            return new R(true,StatusCode.OK,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new R(false,StatusCode.ERROR,"文件上传失败");
        }
    }

    @Override
    public R deleteFile(String fileId) {
        return null;
    }

    @Override
    public R deleteFiles(List<String> fileIds) {
        return null;
    }

    @Override
    public R downloadFile(String fileId) {
        return null;
    }
}
