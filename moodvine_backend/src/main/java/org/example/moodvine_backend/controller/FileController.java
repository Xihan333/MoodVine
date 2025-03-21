package org.example.moodvine_backend.controller;

import org.example.moodvine_backend.model.VO.ResponseData;
import org.example.moodvine_backend.service.FileService;
import org.example.moodvine_backend.utils.R2Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/file")
@RestController
public class FileController {

    @Autowired
    FileService fileService;

    @PostMapping("/upload")
    public ResponseData upload(MultipartFile file) {
        return fileService.upload(file);
    }
}
