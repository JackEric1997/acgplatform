package com.acg.minio.controller;

import com.acg.common.utils.R;
import com.acg.minio.service.MinioService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api(tags = "文件操作管理")
@RestController
@RequestMapping("/file")
public class MinIoController {


    @Autowired
    private MinioService minioService;

    @ApiOperation("上传文件")
    @PostMapping("/file")
    public R upload(@RequestParam("upfile") MultipartFile upfile) throws Exception {
        return minioService.upload(upfile);
    }

    @ApiOperation("上传多个文件")
    @PostMapping("/fileList")
    public R uploadList(List<MultipartFile> upfileList) throws Exception{
        return null;
    }


    @ApiOperation("删除单个文件")
    @GetMapping ("/remove/{fileId}")
    public R deleteOne(@PathVariable("fileId") String fileId) throws Exception{
        return minioService.deleteFile(fileId);
    }

    @ApiOperation("批量删除文件")
    @GetMapping ("/remove")
    public R deleteList(@PathVariable("fileId") List<String> fileIds) throws Exception{
        return minioService.deleteFiles(fileIds);
    }


    @ApiOperation("文件单个下载")
    @GetMapping("/download/{fileId}")
    public R downloadFile(@PathVariable("fileId") String fileId){
        return minioService.downloadFile(fileId);
    }

    @ApiOperation("批量文件下载")
    @GetMapping("/downloadFiles/{fileId}")
    public R downloadFiles(@PathVariable("fileId") String fileId){
        return minioService.downloadFile(fileId);
    }

}
