package com.se1933g01.steam_clone_backend.service;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.google.api.client.http.InputStreamContent;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JFileChooser;

import org.springframework.web.multipart.MultipartFile;

@Service
public class GoogleDriveService {
    private final Drive driveService;

    public GoogleDriveService(Drive driveService) {
        this.driveService = driveService;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(file.getOriginalFilename());

        InputStream fileStream = file.getInputStream();
        InputStreamContent mediaContent = new InputStreamContent("application/octet-stream", fileStream);

        File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();

        return uploadedFile.getId(); // ✅ Returns File ID
    }
    
    public String generateDownloadUrl(String fileId) throws IOException {
        // Retrieve file metadata to get download link
        File fileMetadata = driveService.files().get(fileId).setFields("webContentLink").execute();
        
        return fileMetadata.getWebContentLink(); // Return the file's public download URL
    }




    
    public void makeFilePublic(String fileId) throws IOException {
        Permission permission = new Permission()
                .setType("anyone")  // ✅ Allows anyone to view
                .setRole("reader"); // ✅ Read-only access

        driveService.permissions().create(fileId, permission).execute();
    }
}
