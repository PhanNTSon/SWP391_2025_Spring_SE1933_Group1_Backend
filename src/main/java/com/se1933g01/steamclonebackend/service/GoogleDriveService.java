package com.se1933g01.steamclonebackend.service;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.se1933g01.steamclonebackend.utils.UploadProgressTracker;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.gson.GsonFactory;

import org.springframework.stereotype.Service;


import java.io.IOException;
import java.io.InputStream;


import org.springframework.web.multipart.MultipartFile;


import java.util.ArrayList;

import java.util.List;
import java.util.Map;




@Service
public class GoogleDriveService {
    private final Drive driveService;
    private final HttpRequestFactory requestFactory;
    private static final String RESUMABLE_ENDPOINT = "https://www.googleapis.com/upload/drive/v3/files?uploadType=resumable";
    private final UploadProgressTracker progressTracker;
    public GoogleDriveService(Drive driveService, HttpRequestFactory requestFactory, UploadProgressTracker progressTracker) {
        this.driveService = driveService;
        this.requestFactory = requestFactory;
        this.progressTracker = progressTracker;
    }

    // public List<String> uploadFile(MultipartFile file) throws IOException {
    //     List<String> returnData = new ArrayList<>();
    //     File fileMetadata = new File();
    //     fileMetadata.setName(file.getOriginalFilename());

    //     InputStream fileStream = file.getInputStream();
    //     InputStreamContent mediaContent = new InputStreamContent("application/octet-stream", fileStream);

    //     File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
    //             .setFields("id")
    //             .execute();
    //     returnData.add(uploadedFile.getId());
    //     returnData.add(file.getOriginalFilename());
    //     return returnData; // ✅ Returns File ID
    // }

    public List<String> uploadFile(MultipartFile file) throws IOException {
    List<String> returnData = new ArrayList<>();
    File fileMetadata = new File();
    fileMetadata.setName(file.getOriginalFilename());

    InputStreamContent mediaContent = new InputStreamContent("application/octet-stream", file.getInputStream());
    mediaContent.setLength(file.getSize());
    Drive.Files.Create request = driveService.files()
        .create(fileMetadata, mediaContent)
        .setFields("id");

    MediaHttpUploader uploader = request.getMediaHttpUploader();
    uploader.setChunkSize(MediaHttpUploader.MINIMUM_CHUNK_SIZE); // Optional: tune it

    uploader.setProgressListener(u -> {
        switch (u.getUploadState()) {
            case INITIATION_STARTED -> progressTracker.broadcast("Initiating...");
            case INITIATION_COMPLETE -> progressTracker.broadcast("Connected to Google Drive");
            case MEDIA_IN_PROGRESS -> {
                double percent = u.getProgress() * 100;
                progressTracker.broadcast(percent);
            }
            case MEDIA_COMPLETE -> progressTracker.broadcast("Upload complete");
        }
    });

    File uploadedFile = request.execute();

    returnData.add(uploadedFile.getId());
    returnData.add(file.getOriginalFilename());
    return returnData;
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

    public String createResumableUploadSession(String fileName, String mimeType) throws IOException {
        GenericUrl url = new GenericUrl(RESUMABLE_ENDPOINT);

        Map<String, Object> metadata = Map.of(
            "name", fileName,
            "mimeType", mimeType
        );

        HttpContent content = new JsonHttpContent(GsonFactory.getDefaultInstance(), metadata);

        HttpRequest request = requestFactory.buildPostRequest(url, content);
        request.getHeaders().set("X-Upload-Content-Type", mimeType);
        request.getHeaders().setContentType("application/json");

        HttpResponse response = request.execute();
        return response.getHeaders().getLocation(); // This is your resumable session URL
    }



    








    
}
