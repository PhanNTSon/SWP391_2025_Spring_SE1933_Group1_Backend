package com.se1933g01.steamclonebackend.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.Data;
@Service
@Data
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public List<String> uploadFiles(MultipartFile[] files) throws IOException {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                urls.add(uploadResult.get("url").toString());
            } catch (Exception e) {
                throw new IOException("Failed to process file upload", e);
            }
        }
        return urls;
    }
    public String uploadFile(MultipartFile file) throws IOException {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return uploadResult.get("url").toString();
        } catch (Exception e) {
            throw new IOException("Failed to process file upload", e);
        }
    }

    public void deleteImage(String imageUrl) {
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
