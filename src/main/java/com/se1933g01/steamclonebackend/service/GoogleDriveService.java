package com.se1933g01.steamclonebackend.service;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.google.api.client.http.InputStreamContent;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.drive.DriveScopes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

@Service
public class GoogleDriveService {
    private final Drive driveService;

    public GoogleDriveService(Drive driveService) {
        this.driveService = driveService;
    }

    public List<String> uploadFile(MultipartFile file) throws IOException {
        List<String> returnData = new ArrayList<>();
        File fileMetadata = new File();
        fileMetadata.setName(file.getOriginalFilename());

        InputStream fileStream = file.getInputStream();
        InputStreamContent mediaContent = new InputStreamContent("application/octet-stream", fileStream);

        File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();
        returnData.add(uploadedFile.getId());
        returnData.add(file.getOriginalFilename());
        return returnData; // ✅ Returns File ID
    }

    public String generateDownloadUrl(String fileId) throws IOException {
        // Retrieve file metadata to get download link
        File fileMetadata = driveService.files().get(fileId).setFields("webContentLink").execute();

        return fileMetadata.getWebContentLink(); // Return the file's public download URL
    }

    public void deleteFile(String fileId) throws IOException {
        driveService.files().delete(fileId).execute();
    }

    public void makeFilePublic(String fileId) throws IOException {
        Permission permission = new Permission()
                .setType("anyone") // ✅ Allows anyone to view
                .setRole("reader"); // ✅ Read-only access

        driveService.permissions().create(fileId, permission).execute();
    }

    
}
