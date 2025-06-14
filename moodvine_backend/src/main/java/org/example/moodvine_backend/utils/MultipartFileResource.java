package org.example.moodvine_backend.utils;

import org.springframework.core.io.AbstractResource;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;

public class MultipartFileResource extends AbstractResource {
    private final MultipartFile multipartFile;

    public MultipartFileResource(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    @Override
    public String getDescription() {
        return "MultipartFile resource [" + multipartFile.getName() + "]";
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return multipartFile.getInputStream();
    }

    @Override
    public boolean exists() {
        return !multipartFile.isEmpty();
    }

    @Override
    public long contentLength() throws IOException {
        return multipartFile.getSize();
    }

    @Override
    public String getFilename() {
        return multipartFile.getOriginalFilename();
    }
}