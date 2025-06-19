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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.se1933g01.steam_clone_backend.dto.AddingGameRequestDTO;
import com.se1933g01.steam_clone_backend.dto.BannedUserDTO;
import com.se1933g01.steam_clone_backend.dto.PublisherApplyRequestDTO;
import com.se1933g01.steam_clone_backend.service.GoogleDriveService;
import com.se1933g01.steam_clone_backend.service.RequestService;
import com.se1933g01.steam_clone_backend.service.UserService;

import org.springframework.web.bind.annotation.PathVariable;
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

    // Added by Phan NT Son
    @Autowired
    UserService userService;
    // --!!

    @PatchMapping("/approve/{requestID}")
    public ResponseEntity<Map<String, String>> approveGame(@PathVariable String requestID) {
        try {
            requestService.approveGame(Long.parseLong(requestID));
            Map<String, String> response = new HashMap<>();
            response.put("message", "Game Approved");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Game Approve Failed");
            response.put("e", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PatchMapping("/reject/{requestID}")
    public ResponseEntity<Map<String, String>> rejectGame(@PathVariable String requestID) {
        try {
            requestService.rejectGame(Long.parseLong(requestID));
            Map<String, String> response = new HashMap<>();
            response.put("message", "Game Rejected");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Game Reject Failed");
            response.put("e", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PatchMapping("/approvepublisher/{requestID}")
    public ResponseEntity<Map<String,String>> approvePublisher(@PathVariable String requestID){
        try {
            requestService.approvePublisher(Long.parseLong(requestID));
            Map<String,String> response = new HashMap<>();
            response.put("message", "This user are now publisher");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String,String> response = new HashMap<>();
            response.put("message", "Failed to approve");
            response.put("e", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    @PatchMapping("/rejectpublisher/{requestID}")
    public ResponseEntity<Map<String,String>> rejectPublisher(@PathVariable String requestID){
        try {
            requestService.rejectPublisher(Long.parseLong(requestID));
            Map<String,String> response = new HashMap<>();
            response.put("message", "User Rejected");
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
    @GetMapping("/publisherApplyRequest/{page}")
    public ResponseEntity<Page<PublisherApplyRequestDTO>> getPublisherApplyRequest(@PathVariable int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("requestId").descending());// Hard-coded size to 10
        Page<PublisherApplyRequestDTO> publisherApplyRequestPage = requestService.getAllPublisherApplyRequest(pageable);
        return ResponseEntity.ok(publisherApplyRequestPage);
    }
    @GetMapping("/gameRequest/details/{id}")
    public ResponseEntity<AddingGameRequestDTO> getGameRequest(@PathVariable Long id) {
        AddingGameRequestDTO gameDetails = requestService.getGameDetails(id);
        if (gameDetails == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(gameDetails);
    }
    @GetMapping("/publisherApplyRequest/details/{id}")
    public ResponseEntity<PublisherApplyRequestDTO> getPublisherApplyRequest(@PathVariable Long id) {
        PublisherApplyRequestDTO publisherApplyRequestDetails = requestService.getPublisherDetails(id);
        if (publisherApplyRequestDetails == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(publisherApplyRequestDetails);
    }
    @GetMapping("/download/{fileId}")
    public ResponseEntity<String> downloadFile(@PathVariable("fileId") String fileId) throws IOException {
        String downloadUrl = googleDriveService.generateDownloadUrl(fileId);
        System.out.println(downloadUrl);
        return ResponseEntity.ok(downloadUrl);
    }

    /**
     * @apiNote Author Phan NT Son
     * @return
     */
    @GetMapping("/banned-users/{page}")
    public ResponseEntity<Page<BannedUserDTO>> getAllBannedUser(@PathVariable(name = "page") int pageNum) {
        Page<BannedUserDTO> result = userService.getAllBannedUser(pageNum);
        return ResponseEntity.ok(result);
    }
}
