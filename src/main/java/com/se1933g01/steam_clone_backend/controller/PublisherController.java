package com.se1933g01.steam_clone_backend.controller;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.se1933g01.steam_clone_backend.dto.AddingGameRequestDTO;
import com.se1933g01.steam_clone_backend.entity.request.AddingGameRequest;
import com.se1933g01.steam_clone_backend.entity.request.Request;
import com.se1933g01.steam_clone_backend.service.CloudinaryService;
import com.se1933g01.steam_clone_backend.service.PublisherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/publisher")
public class PublisherController {
    @Autowired
    private PublisherService publisherService;

    @Autowired
    private CloudinaryService cloudinaryService;
    
    @PostMapping("/addGame")
    public ResponseEntity<Map<String,String>> addGame(@RequestBody AddingGameRequestDTO addingGameRequestDTO) {
        try {
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
    
}
