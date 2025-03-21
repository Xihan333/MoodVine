package org.example.moodvine_backend.service;

import org.example.moodvine_backend.model.VO.ResponseData;
import org.example.moodvine_backend.utils.R2Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileService {

    @Autowired
    R2Utils r2Utils;

    public ResponseData upload(MultipartFile file) {
        try {
            String url = r2Utils.uploadFile(file);
            return new ResponseData(200, url, "File uploaded successfully");
        } catch (IOException e) {
            return new ResponseData(500, null, "Failed to upload file: " + e.getMessage());
        }
    }
}
