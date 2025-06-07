package com.se1933g01.steam_clone_backend.service;

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
    public List<String> uploadFiles(MultipartFile[] files){
        List<String> urls = new ArrayList<>();
        for(MultipartFile file: files){
            try {
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                urls.add(uploadResult.get("url").toString());
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return urls;
    }
}
