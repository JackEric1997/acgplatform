package com.acg.minio.service;

import com.acg.common.utils.R;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MinioService {

    R upload(MultipartFile file);

    R deleteFile(String fileId);

    R deleteFiles(List<String> fileIds);

    R downloadFile(String fileId);
}
