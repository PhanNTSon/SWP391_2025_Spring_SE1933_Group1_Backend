package com.se1933g01.steamclonebackend.service;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
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
    private final String bucketName = "steam";
    private final String r2Endpoint = "https://ce2d1d4b1db731e41135ed96b83b118b.r2.cloudflarestorage.com";
    private String accessKey;

    private String secretKey;

    public R2StorageService(Environment env,UploadProgressTracker progressTracker) {
        this.progressTracker = progressTracker;
        this.accessKey = env.getProperty("r2.access-key");
        this.secretKey = env.getProperty("r2.secret-key");
        this.s3Client = S3Client.builder()
            .endpointOverride(URI.create(r2Endpoint))
            .region(Region.US_EAST_1) // R2 doesn't care; pick any region
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
            ))
            .build();
    }

    public List<String> uploadFile(MultipartFile file) throws IOException {
        List<String> returnData = new ArrayList<>();
        String key = file.getOriginalFilename();// here to filekey

        progressTracker.broadcast("Initiating upload...");

        PutObjectRequest putReq = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(file.getContentType())
            .build();

        progressTracker.broadcast("Uploading...");

        s3Client.putObject(putReq, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        progressTracker.broadcast("Upload complete");

        returnData.add(key); // Used as "fileId" equivalent
        returnData.add(file.getOriginalFilename());
        return returnData;
    }

public String generateDownloadUrl(String key) {
    AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

    S3Presigner presigner = S3Presigner.builder()
        .endpointOverride(URI.create(r2Endpoint)) // Cloudflare R2 endpoint
        .region(Region.of("auto")) // R2 doesn’t require a specific region
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .build();

    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofHours(1)) // Expires in 1 hour
        .getObjectRequest(getObjectRequest)
        .build();

    URL presignedUrl = presigner.presignGetObject(presignRequest).url();
    presigner.close();

    return presignedUrl.toString();
}



    public void deleteFile(String key) {
        DeleteObjectRequest deleteReq = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();

        s3Client.deleteObject(deleteReq);
    }

    public String generatePresignedUploadUrl(String fileName, String contentType) {
        String key = UUID.randomUUID().toString();

        PutObjectRequest objectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(contentType)
            .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(10)) // URL valid for 10 minutes
            .putObjectRequest(objectRequest)
            .build();

        S3Presigner presigner = S3Presigner.builder()
            .endpointOverride(URI.create(r2Endpoint))
            .region(Region.US_EAST_1)
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
            ))
            .build();

        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
        return presignedRequest.url().toString(); // 🚀 Send this to frontend
    }
}


