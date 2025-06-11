package com.se1933g01.steam_clone_backend.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.se1933g01.steam_clone_backend.dto.AddingGameRequestDTO;
import com.se1933g01.steam_clone_backend.service.GoogleDriveService;
import com.se1933g01.steam_clone_backend.service.RequestService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    RequestService requestService;
    @Autowired
    GoogleDriveService googleDriveService;
    @PatchMapping("/approve/{requestID}")
    public ResponseEntity<Map<String,String>> approveGame(@PathVariable String requestID){
        try {
            requestService.approveGame(Long.parseLong(requestID));
            Map<String,String> response = new HashMap<>();
            response.put("message", "Game Approved");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String,String> response = new HashMap<>();
            response.put("message", "Game Approve Failed");
            response.put("e", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    @PatchMapping("/reject/{requestID}")
    public ResponseEntity<Map<String,String>> rejectGame(@PathVariable String requestID){
        try {
            requestService.rejectGame(Long.parseLong(requestID));
            Map<String,String> response = new HashMap<>();
            response.put("message", "Game Rejected");
            return ResponseEntity.ok(response);
        }catch (Exception e) {
            Map<String,String> response = new HashMap<>();
            response.put("message", "Game Reject Failed");
            response.put("e", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/gameRequest/{page}")
    public ResponseEntity<Page<AddingGameRequestDTO>> getGameRequest(@PathVariable int page) {
    Pageable pageable = PageRequest.of(page, 10, Sort.by("requestId").descending());// Hard-coded size to 10
    Page<AddingGameRequestDTO> gameRequestPage = requestService.getAllAddingGameRequest(pageable);
    return ResponseEntity.ok(gameRequestPage);
    }

    @GetMapping("/gameRequest/details/{id}")
    public ResponseEntity<AddingGameRequestDTO> getGameRequest(@PathVariable Long id) {
        AddingGameRequestDTO gameDetails = requestService.getGameDetails(id);
        return ResponseEntity.ok(gameDetails);
    }
    @GetMapping("/download/{fileId}")
    public ResponseEntity<String> downloadFile(@PathVariable("fileId") String fileId) throws IOException {
        String downloadUrl = googleDriveService.generateDownloadUrl(fileId);
        System.out.println(downloadUrl);
        return ResponseEntity.ok(downloadUrl);
    }
    
}
