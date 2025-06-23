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
    private static final String RESPONSE_MESSAGE_KEY = "message";
    private final RequestService requestService;
    private final GoogleDriveService googleDriveService;
    private final RequestService publisherService;
    private final CloudinaryService cloudinaryService;
    private final UserService userService;

    public RequestController(
    RequestService requestService,
    GoogleDriveService googleDriveService,
    RequestService publisherService,
    CloudinaryService cloudinaryService,
    UserService userService
) {
    this.requestService = requestService;
    this.googleDriveService = googleDriveService;
    this.publisherService = publisherService;
    this.cloudinaryService = cloudinaryService;
    this.userService = userService;
}
    private ResponseEntity<Map<String, String>> handleAction(Runnable action, String successMsg, String failMsg) {
        Map<String, String> response = new HashMap<>();
        try {
            action.run();
            response.put(RESPONSE_MESSAGE_KEY, successMsg);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put(RESPONSE_MESSAGE_KEY, failMsg);
            response.put("e", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PatchMapping("/game/approve/{requestID}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> approveGame(@PathVariable String requestID) {
        return handleAction(() -> requestService.approveGame(Long.parseLong(requestID)),
                "Game Approved", "Game Approve Failed");
    }

    @PatchMapping("/game/reject/{requestID}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> rejectGame(@PathVariable String requestID) {
        return handleAction(() -> requestService.rejectGame(Long.parseLong(requestID)),
                "Game Rejected", "Game Reject Failed");
    }

    @PatchMapping("/publisher/approve/{requestID}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> approvePublisher(@PathVariable String requestID) {
        return handleAction(() -> requestService.approvePublisher(Long.parseLong(requestID)),
                "This user is now a publisher", "Failed to approve");
    }

    @PatchMapping("/publisher/reject/{requestID}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> rejectPublisher(@PathVariable String requestID) {
        return handleAction(() -> requestService.rejectPublisher(Long.parseLong(requestID)),
                "User Rejected", "Game Reject Failed");
    }

    @PatchMapping("/feedback/approve/{requestID}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> approveFeedback(@PathVariable String requestID) {
        return handleAction(() -> requestService.approveFeedback(Long.parseLong(requestID)),
                "Feedback Approved", "Feedback Approve Failed");
    }
    @PatchMapping("/feedback/reject/{requestID}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> rejectFeedback(@PathVariable String requestID) {
        return handleAction(() -> requestService.rejectFeedback(Long.parseLong(requestID)),
                "Feedback Rejected", "Feedback Reject Failed");
    }

    @GetMapping("/game/{page}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AddingGameRequestDTO>> getGameRequest(@PathVariable int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("requestId").descending());
        return ResponseEntity.ok(requestService.getAllAddingGameRequest(pageable));
    }

    @GetMapping("/publisher/{page}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PublisherApplyRequestDTO>> getPublisherApplyRequest(@PathVariable int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("requestId").descending());
        return ResponseEntity.ok(requestService.getAllPublisherApplyRequest(pageable));
    }

    @GetMapping("/feedback/{page}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<FeedbackDTO>> getFeedback(@PathVariable int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("requestId").descending());
        return ResponseEntity.ok(requestService.getAllFeedback(pageable));
    }

    @GetMapping("/game/details/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AddingGameRequestDTO> getGameRequest(@PathVariable Long id) {
        AddingGameRequestDTO details = requestService.getGameDetails(id);
        return details == null
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
                : ResponseEntity.ok(details);
    }

    @GetMapping("/publisher/details/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PublisherApplyRequestDTO> getPublisherApplyRequest(@PathVariable Long id) {
        PublisherApplyRequestDTO details = requestService.getPublisherDetails(id);
        return details == null
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
                : ResponseEntity.ok(details);
    }

    @GetMapping("/feedback/details/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FeedbackDTO> getFeedbackDetails(@PathVariable Long id) {
        FeedbackDTO details = requestService.getFeedbackDetails(id);
        return details == null
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
                : ResponseEntity.ok(details);
    }
    

    @GetMapping("/banned-users/{page}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BannedUserDTO>> getAllBannedUser(@PathVariable("page") int pageNum) {
        return ResponseEntity.ok(userService.getAllBannedUser(pageNum));
    }

    @PostMapping("/game/add")
    @PreAuthorize("hasRole('PUBLISHER')")
    public ResponseEntity<Map<String, String>> addGame(
            @RequestBody AddingGameRequestDTO dto, @AuthenticationPrincipal CustomUserDetail me) {
        try {
            publisherService.addGame(dto, me.getUser().getUserId());
            return ResponseEntity.ok(Map.of(RESPONSE_MESSAGE_KEY, "Game added successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(RESPONSE_MESSAGE_KEY, e.getMessage()));
        }
    }

    @PostMapping("/image/upload")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("files") MultipartFile[] files) {
        try {
            List<String> urls = cloudinaryService.uploadFiles(files);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put(RESPONSE_MESSAGE_KEY, "Files uploaded successfully");
            response.put("imageUrls", urls);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put(RESPONSE_MESSAGE_KEY, e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/file/upload")
    @PreAuthorize("hasRole('PUBLISHER')")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        List<String> fileId = googleDriveService.uploadFile(file);
        googleDriveService.makeFilePublic(fileId.get(0));
        return ResponseEntity.ok(Map.of(
                RESPONSE_MESSAGE_KEY, "Upload Successful!",
                "fileId", fileId.get(0),
                "fileName", fileId.get(1)));
    }

    @GetMapping("/file/download/{fileId}")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<String> downloadFile(@PathVariable("fileId") String fileId) throws IOException {
        return ResponseEntity.ok(googleDriveService.generateDownloadUrl(fileId));
    }

    @DeleteMapping("/file/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteGame(@PathVariable("id") String fileId) throws IOException {
        googleDriveService.deleteFile(fileId);
        return ResponseEntity.ok(Map.of(RESPONSE_MESSAGE_KEY, "File deleted successfully"));
    }

    @PostMapping("/publisher/send")
    @PreAuthorize("hasRole('STANDARD')")
    public ResponseEntity<Map<String, Object>> sendPublisherApplyRequest(
            @RequestBody PublisherApplyRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetail me
            ) {
        Map<String, Object> response = new HashMap<>();
        try {
            requestService.addPublisher(dto, me.getUser().getUserId());
            response.put(RESPONSE_MESSAGE_KEY, "Sending publisher apply request successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/feedback/send")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER')")
    public ResponseEntity<Map<String, Object>> sendFeedback(@RequestBody FeedbackDTO feedbackDTO,
            @AuthenticationPrincipal CustomUserDetail me
            ) {
        Map<String, Object> response = new HashMap<>();
        try {
            requestService.addFeedback(feedbackDTO, me.getUser().getUserId());
            response.put(RESPONSE_MESSAGE_KEY, "Sending feedback successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/game/exist/check")
    @PreAuthorize("hasAnyRole('PUBLISHER')")
    public ResponseEntity<Map<String, Object>> checkGameExist(@RequestParam String gameName) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean check = requestService.checkForGameExist(gameName);
            response.put("debug", gameName);
            response.put(RESPONSE_MESSAGE_KEY, check);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(response);
        }
    }

}

