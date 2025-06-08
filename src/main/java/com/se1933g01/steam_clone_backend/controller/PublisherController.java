// package com.se1933g01.steam_clone_backend.controller;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.se1933g01.steam_clone_backend.dto.AddingGameRequestDTO;
import com.se1933g01.steam_clone_backend.entity.request.AddingGameRequest;
import com.se1933g01.steam_clone_backend.entity.request.Request;
import com.se1933g01.steam_clone_backend.service.CloudinaryService;
import com.se1933g01.steam_clone_backend.service.GoogleDriveService;
import com.se1933g01.steam_clone_backend.service.RequestService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


// @RestController
// @RequestMapping("/publisher")
// public class PublisherController {
//     @Autowired
//     private RequestService publisherService;

    @Autowired
    private CloudinaryService cloudinaryService;
    
    @Autowired
    private GoogleDriveService googleDriveService;

    @PostMapping("/addGame")
    public ResponseEntity<Map<String,String>> addGame(@RequestBody AddingGameRequestDTO addingGameRequestDTO) {
        try {
            System.out.println(addingGameRequestDTO);
            publisherService.addGame(addingGameRequestDTO,2L);
            Map<String,String> map = Map.of("message","Game added successfully");
            return ResponseEntity.ok(map);
        }catch(Exception e) {
            Map<String,String> map = Map.of("message",e.getMessage());
            return ResponseEntity.badRequest().body(map);
        }
    }
    @PostMapping("/uploadImage")
    public ResponseEntity<Map<String,Object>> uploadImage(@RequestParam("files") MultipartFile[] files) {
        try {
            List<String> urls = cloudinaryService.uploadFiles(files);
            Map<String,Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Files uploaded successfully");
            response.put("imageUrls", urls);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String,Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        List<String> fileId = googleDriveService.uploadFile(file);
        googleDriveService.makeFilePublic(fileId.get(0));
        return ResponseEntity.ok(Map.of("message", "Upload Successful!", "fileId", fileId.get(0),"fileName",fileId.get(1)));
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<String> downloadFile(@PathVariable("fileId") String fileId) throws IOException {
        String downloadUrl = googleDriveService.generateDownloadUrl(fileId);
        System.out.println(downloadUrl);
        return ResponseEntity.ok(downloadUrl);
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String,String>> deleteGame(@PathVariable("id") String fileId) throws IOException {
        googleDriveService.deleteFile(fileId);
        Map<String,String> map = Map.of("message","File deleted successfully");
        return ResponseEntity.ok(map);
    }
    
// }
