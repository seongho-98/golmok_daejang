package com.golmok.golmok_daejang.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @Value("${file.upload.base-url:/images}")
    private String baseUrl;

    public String saveBase64Image(String base64Data) {
        try {
            String base64 = base64Data.contains(",") ? base64Data.split(",")[1] : base64Data;
            byte[] imageBytes = Base64.getDecoder().decode(base64);

            String filename = UUID.randomUUID() + ".png";
            Path path = Paths.get(uploadPath).toAbsolutePath();
            Files.createDirectories(path);
            Files.write(path.resolve(filename), imageBytes);

            return baseUrl + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패", e);
        }
    }
}
