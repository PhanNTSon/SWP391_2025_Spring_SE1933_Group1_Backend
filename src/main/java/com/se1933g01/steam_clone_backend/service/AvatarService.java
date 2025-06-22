package com.se1933g01.steam_clone_backend.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AvatarService {

    @Autowired
    private GoogleDriveService googleDriveService;
    
    public String store(MultipartFile file) throws IOException {
        // Tạo một tên file duy nhất
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        return googleDriveService.uploadFile(file, fileName);
    }
}
