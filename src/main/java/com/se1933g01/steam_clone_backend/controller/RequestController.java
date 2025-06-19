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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.se1933g01.steam_clone_backend.dto.AddingGameRequestDTO;
import com.se1933g01.steam_clone_backend.dto.BannedUserDTO;
import com.se1933g01.steam_clone_backend.dto.FeedbackDTO;
import com.se1933g01.steam_clone_backend.dto.PublisherApplyRequestDTO;
import com.se1933g01.steam_clone_backend.entity.user.CustomUserDetail;
import com.se1933g01.steam_clone_backend.service.CloudinaryService;
import com.se1933g01.steam_clone_backend.service.GoogleDriveService;
import com.se1933g01.steam_clone_backend.service.RequestService;
import com.se1933g01.steam_clone_backend.service.UserService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;

/**
 * @author Hoang VUBE
 */
@RestController
@RequestMapping("/request")
public class RequestController {
    @Autowired
    RequestService requestService;
    @Autowired
    GoogleDriveService googleDriveService;

    @Autowired
    private RequestService publisherService;

    @Autowired
    private CloudinaryService cloudinaryService;

    // Added by Phan NT Son
    @Autowired
    UserService userService;
    // --!!

    @PatchMapping("/approve/{requestID}")
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> approvePublisher(@PathVariable String requestID) {
        try {
            requestService.approvePublisher(Long.parseLong(requestID));
            Map<String, String> response = new HashMap<>();
            response.put("message", "This user are now publisher");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to approve");
            response.put("e", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PatchMapping("/rejectpublisher/{requestID}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> rejectPublisher(@PathVariable String requestID) {
        try {
            requestService.rejectPublisher(Long.parseLong(requestID));
            Map<String, String> response = new HashMap<>();
            response.put("message", "User Rejected");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Game Reject Failed");
            response.put("e", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/gameRequest/{page}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AddingGameRequestDTO>> getGameRequest(@PathVariable int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("requestId").descending());// Hard-coded size to 10
        Page<AddingGameRequestDTO> gameRequestPage = requestService.getAllAddingGameRequest(pageable);
        return ResponseEntity.ok(gameRequestPage);
    }

    @GetMapping("/publisherApplyRequest/{page}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PublisherApplyRequestDTO>> getPublisherApplyRequest(@PathVariable int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("requestId").descending());// Hard-coded size to 10
        Page<PublisherApplyRequestDTO> publisherApplyRequestPage = requestService.getAllPublisherApplyRequest(pageable);
        return ResponseEntity.ok(publisherApplyRequestPage);
    }

    @GetMapping("/gameRequest/details/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AddingGameRequestDTO> getGameRequest(@PathVariable Long id) {
        AddingGameRequestDTO gameDetails = requestService.getGameDetails(id);
        if (gameDetails == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(gameDetails);
    }

    @GetMapping("/publisherApplyRequest/details/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PublisherApplyRequestDTO> getPublisherApplyRequest(@PathVariable Long id) {
        PublisherApplyRequestDTO publisherApplyRequestDetails = requestService.getPublisherDetails(id);
        if (publisherApplyRequestDetails == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(publisherApplyRequestDetails);
    }

    /**
     * @apiNote Author Phan NT Son
     * @return
     */
    @GetMapping("/banned-users/{page}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BannedUserDTO>> getAllBannedUser(@PathVariable(name = "page") int pageNum) {
        Page<BannedUserDTO> result = userService.getAllBannedUser(pageNum);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/addGame")
    @PreAuthorize("hasRole('PUBLISHER')")
    public ResponseEntity<Map<String, String>> addGame(
            @RequestBody AddingGameRequestDTO addingGameRequestDTO, @AuthenticationPrincipal CustomUserDetail me) {
        try {
            publisherService.addGame(addingGameRequestDTO, me.getUser().getUserID());
            Map<String, String> map = Map.of("message", "Game added successfully");
            return ResponseEntity.ok(map);
        } catch (Exception e) {
            Map<String, String> map = Map.of("message", e.getMessage());
            return ResponseEntity.badRequest().body(map);
        }
    }

    @PostMapping("/uploadImage")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("files") MultipartFile[] files) {
        try {
            List<String> urls = cloudinaryService.uploadFiles(files);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Files uploaded successfully");
            response.put("imageUrls", urls);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('PUBLISHER')")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        List<String> fileId = googleDriveService.uploadFile(file);
        googleDriveService.makeFilePublic(fileId.get(0));
        return ResponseEntity
                .ok(Map.of("message", "Upload Successful!", "fileId", fileId.get(0), "fileName", fileId.get(1)));
    }

    @GetMapping("/download/{fileId}")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<String> downloadFile(@PathVariable("fileId") String fileId) throws IOException {
        String downloadUrl = googleDriveService.generateDownloadUrl(fileId);
        System.out.println(downloadUrl);
        return ResponseEntity.ok(downloadUrl);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteGame(@PathVariable("id") String fileId) throws IOException {
        googleDriveService.deleteFile(fileId);
        Map<String, String> map = Map.of("message", "File deleted successfully");
        return ResponseEntity.ok(map);
    }

    @PostMapping("/sendpublisher")
    @PreAuthorize("hasRole('STANDARD')")
    public ResponseEntity<Map<String, Object>> sendPublisherApplyRequest(
            @RequestBody PublisherApplyRequestDTO publisherApplyRequestDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            requestService.addPublisher(publisherApplyRequestDTO, 2L);
            response.put("message", "Sending publisher apply request successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/sendfeedback")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER')")
    public ResponseEntity<Map<String, Object>> sendFeedback(@RequestBody FeedbackDTO feedbackDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            requestService.addFeedback(feedbackDTO, 2L);
            response.put("message", "Sending feedback successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
