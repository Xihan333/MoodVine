package org.example.moodvine_backend.utils;

import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class UrlMultipartFile implements MultipartFile {
    private final UrlResource resource;
    private final String filename;
    private final String contentType;

    public UrlMultipartFile(String fileUrl) throws IOException {
        this.resource = new UrlResource(new URL(fileUrl));
        this.filename = resource.getFilename();
        this.contentType = determineContentType(filename);
    }

    private String determineContentType(String filename) {
        // 根据文件扩展名判断Content-Type
        if (filename == null) return "application/octet-stream";
        if (filename.endsWith(".mp3")) return "audio/mpeg";
        if (filename.endsWith(".wav")) return "audio/wav";
        return "application/octet-stream";
    }

    @Override
    public String getName() {
        return "file";
    }

    @Override
    public String getOriginalFilename() {
        return filename;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return !resource.exists();
    }

    @Override
    public long getSize() {
        try {
            return resource.contentLength();
        } catch (IOException e) {
            return 0;
        }
    }

    @Override
    public byte[] getBytes() throws IOException {
        return resource.getInputStream().readAllBytes();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return resource.getInputStream();
    }

    @Override
    public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
        resource.getInputStream().transferTo(new java.io.FileOutputStream(dest));
    }
}