package com.se1933g01.steamclonebackend.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.Data;

@Service
@Data
public class CloudinaryService {
    private final Cloudinary cloudinary;
    private final boolean isLocalMock;
    private final String localUploadDir = "uploads/cloudinary/";

    public CloudinaryService(Cloudinary cloudinary, Environment env) {
        this.cloudinary = cloudinary;
        String cloudName = env.getProperty("cloudinary.cloud-name");
        this.isLocalMock = cloudName == null || cloudName.trim().isEmpty() || "mock".equalsIgnoreCase(cloudName) || "davzqwcoq".equalsIgnoreCase(cloudName);
        if (isLocalMock) {
            System.out.println("INFO: Cloudinary cloud-name is set to '" + cloudName + "'. Enabling Local Mock Storage.");
            File dir = new File(localUploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
    }

    public List<String> uploadFiles(MultipartFile[] files) throws IOException {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            urls.add(uploadFile(file));
        }
        return urls;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        if (isLocalMock) {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + extension;
            Path destination = Paths.get(localUploadDir).resolve(fileName);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/cloudinary/" + fileName;
        }

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return uploadResult.get("url").toString();
        } catch (Exception e) {
            throw new IOException("Failed to process file upload", e);
        }
    }

    public void deleteImage(String imageUrl) {
        if (isLocalMock) {
            if (imageUrl != null && imageUrl.contains("/uploads/cloudinary/")) {
                String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                Path file = Paths.get(localUploadDir).resolve(fileName);
                try {
                    Files.deleteIfExists(file);
                    System.out.println("Local Mock: Deleted image " + fileName);
                } catch (IOException e) {
                    System.err.println("Local Mock: Failed to delete image " + fileName + ": " + e.getMessage());
                }
            }
            return;
        }

        try {
            String publicId = imageUrl.replaceAll(".*/([^/]+)\\.[^.]+$", "$1");
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            String outcome = (String) result.get("result");

            if ("ok".equals(outcome)) {
                System.out.println("Image deleted successfully: " + publicId);
            } else if ("not found".equals(outcome)) {
                System.out.println("Image not found: " + publicId);
            } else {
                System.out.println("Cloudinary delete failed: " + outcome);
            }
        } catch (IOException e) {
            System.err.println("Exception deleting image: " + e.getMessage());
        }
    }
}
