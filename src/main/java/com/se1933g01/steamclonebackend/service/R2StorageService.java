package com.se1933g01.steamclonebackend.service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.se1933g01.steamclonebackend.utils.UploadProgressTracker;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

@Service
public class R2StorageService {
    private final S3Client s3Client;
    private final UploadProgressTracker progressTracker;
    private final String bucketName;
    private final String r2Endpoint;
    private final String accessKey;
    private final String secretKey;
    private final boolean isLocalMock;
    private final String localUploadDir = "uploads/r2/";

    public R2StorageService(Environment env, UploadProgressTracker progressTracker) {
        this.progressTracker = progressTracker;
        this.accessKey = env.getProperty("r2.access-key");
        this.secretKey = env.getProperty("r2.secret-key");
        this.bucketName = env.getProperty("r2.bucket-name", "steam");
        this.r2Endpoint = env.getProperty("r2.endpoint", "https://ce2d1d4b1db731e41135ed96b83b118b.r2.cloudflarestorage.com");
        
        this.isLocalMock = accessKey == null || accessKey.trim().isEmpty() || "mock".equalsIgnoreCase(accessKey) ||
                           secretKey == null || secretKey.trim().isEmpty() || "mock".equalsIgnoreCase(secretKey);

        if (!isLocalMock) {
            this.s3Client = S3Client.builder()
                .endpointOverride(URI.create(r2Endpoint))
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .build();
        } else {
            this.s3Client = null;
            System.out.println("INFO: Cloudflare R2 credentials set to 'mock' or empty. Enabling Local Mock Storage.");
            File dir = new File(localUploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
    }

    public List<String> uploadFile(MultipartFile file) throws IOException {
        String key = file.getOriginalFilename();
        if (isLocalMock) {
            progressTracker.broadcast("Initiating mock upload...");
            Path destination = Paths.get(localUploadDir).resolve(key);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            progressTracker.broadcast("Mock Upload complete");
            
            List<String> returnData = new ArrayList<>();
            returnData.add(key);
            returnData.add(key);
            return returnData;
        }

        List<String> returnData = new ArrayList<>();
        progressTracker.broadcast("Initiating upload...");

        PutObjectRequest putReq = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(file.getContentType())
            .build();

        progressTracker.broadcast("Uploading...");

        s3Client.putObject(putReq, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        progressTracker.broadcast("Upload complete");

        returnData.add(key);
        returnData.add(file.getOriginalFilename());
        return returnData;
    }

    public String generateDownloadUrl(String key) {
        if (isLocalMock) {
            return "/uploads/r2/" + key;
        }

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        try (S3Presigner presigner = S3Presigner.builder()
            .endpointOverride(URI.create(r2Endpoint))
            .region(Region.of("auto"))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build()) {

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(1))
                .getObjectRequest(getObjectRequest)
                .build();

            URL presignedUrl = presigner.presignGetObject(presignRequest).url();
            return presignedUrl.toString();
        }
    }

    public void deleteFile(String key) {
        if (isLocalMock) {
            Path file = Paths.get(localUploadDir).resolve(key);
            try {
                Files.deleteIfExists(file);
                System.out.println("Local Mock: Deleted R2 file " + key);
            } catch (IOException e) {
                System.err.println("Local Mock: Failed to delete R2 file " + key + ": " + e.getMessage());
            }
            return;
        }

        DeleteObjectRequest deleteReq = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();

        s3Client.deleteObject(deleteReq);
    }

    public String generatePresignedUploadUrl(String fileName, String contentType) {
        String key = UUID.randomUUID().toString() + "-" + fileName;
        if (isLocalMock) {
            // Return local upload endpoint url
            return "/request/file/mock-upload/" + key;
        }

        PutObjectRequest objectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(contentType)
            .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(10))
            .putObjectRequest(objectRequest)
            .build();

        try (S3Presigner presigner = S3Presigner.builder()
            .endpointOverride(URI.create(r2Endpoint))
            .region(Region.US_EAST_1)
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
            ))
            .build()) {

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
            return presignedRequest.url().toString();
        }
    }

    // Direct upload helper for local mock PUT request
    public void saveMockUploadedFile(String key, byte[] fileBytes) throws IOException {
        Path destination = Paths.get(localUploadDir).resolve(key);
        Files.write(destination, fileBytes);
        System.out.println("Local Mock: Saved uploaded file to " + destination.toAbsolutePath());
    }
}
