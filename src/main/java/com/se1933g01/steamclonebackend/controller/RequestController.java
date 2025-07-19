package com.se1933g01.steamclonebackend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.se1933g01.steamclonebackend.dto.AddingGameRequestDTO;
import com.se1933g01.steamclonebackend.dto.BannedUserDTO;
import com.se1933g01.steamclonebackend.dto.FeedbackDTO;
import com.se1933g01.steamclonebackend.dto.PublisherApplyRequestDTO;
import com.se1933g01.steamclonebackend.entity.user.CustomUserDetail;
import com.se1933g01.steamclonebackend.service.CloudinaryService;
import com.se1933g01.steamclonebackend.service.R2StorageService;
import com.se1933g01.steamclonebackend.service.RequestService;
import com.se1933g01.steamclonebackend.service.UserService;
import com.se1933g01.steamclonebackend.utils.UploadProgressTracker;

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
    private static final String REQUEST_ID = "requestId";
    private final RequestService requestService;
    private final RequestService publisherService;
    private final CloudinaryService cloudinaryService;
    private final UserService userService;
    private final UploadProgressTracker tracker;
    private final R2StorageService r2StorageService;
    public RequestController(
    RequestService requestService,
    RequestService publisherService,
    CloudinaryService cloudinaryService,
    UserService userService, UploadProgressTracker tracker,
    R2StorageService r2StorageService
) {
    this.requestService = requestService;
    this.publisherService = publisherService;
    this.cloudinaryService = cloudinaryService;
    this.userService = userService;
    this.tracker = tracker;
    this.r2StorageService = r2StorageService;
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
    public ResponseEntity<Map<String, String>> rejectGame(@PathVariable String requestID,@RequestBody AddingGameRequestDTO addingGameRequestDTO) {
        return handleAction(() -> requestService.rejectGame(Long.parseLong(requestID),addingGameRequestDTO.getDeclineMessage()),
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
    public ResponseEntity<Map<String, String>> approveFeedback(@PathVariable String requestID,@RequestBody FeedbackDTO feedbackDTO) {
        return handleAction(() -> requestService.approveFeedback(Long.parseLong(requestID),feedbackDTO.getResponse()),
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
        Pageable pageable = PageRequest.of(page, 10, Sort.by(REQUEST_ID).descending());
        return ResponseEntity.ok(requestService.getAllAddingGameRequest(pageable));
    }

    @GetMapping("/publisher/{page}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PublisherApplyRequestDTO>> getPublisherApplyRequest(@PathVariable int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(REQUEST_ID).descending());
        return ResponseEntity.ok(requestService.getAllPublisherApplyRequest(pageable));
    }

    @GetMapping("/feedback/{page}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<FeedbackDTO>> getFeedback(@PathVariable int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(REQUEST_ID).descending());
        return ResponseEntity.ok(requestService.getAllFeedback(pageable));
    }

    @GetMapping("/feedback/user/{page}")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER')")
    public ResponseEntity<Page<FeedbackDTO>> getFeedbackFromUser(@PathVariable int page, @AuthenticationPrincipal CustomUserDetail me) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(REQUEST_ID).descending());
        return ResponseEntity.ok(requestService.getAllFeedbackFromUser(me.getUser().getUserId(), pageable));
    }
    

    @GetMapping("/game/details/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PUBLISHER')")
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

    @GetMapping("/publisher/user/details/{id}")
    @PreAuthorize("hasAnyRole('STANDARD')")
    public ResponseEntity<PublisherApplyRequestDTO> getUserPublisherApplyDetails(@AuthenticationPrincipal CustomUserDetail me) {
        PublisherApplyRequestDTO details = requestService.getUserPublisherApplyDetails(me.getUser().getUserId());
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
    
    @GetMapping("/feedback/user/details/{id}")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER')")
    public ResponseEntity<FeedbackDTO> getUserFeedbackDetails(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetail me) {
        FeedbackDTO details = requestService.getUserFeedbackDetails(id, me.getUser().getUserId());
        return details == null
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
                : ResponseEntity.ok(details);
    }

    @DeleteMapping("/feedback/user/{id}")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER')")
    public ResponseEntity<Map<String, String>> deleteUserFeedback(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetail me) {
        requestService.deleteFeedback(id, me.getUser().getUserId());
        return ResponseEntity.ok(Map.of(RESPONSE_MESSAGE_KEY, "Feedback deleted successfully"));
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

    @PostMapping("/image/delete/{url}")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<Map<String, String>> deleteImage(@PathVariable("url") String imageUrl) {
        cloudinaryService.deleteImage(imageUrl);
        return ResponseEntity.ok(Map.of(RESPONSE_MESSAGE_KEY, "Image deleted successfully"));
    }
    

    @PostMapping("/file/upload")
    @PreAuthorize("hasRole('PUBLISHER')")
    public ResponseEntity<Map<String, String>> generateUploadLink(
        @RequestParam("fileName") String fileName,
        @RequestParam("contentType") String contentType
    ) {
        String uploadUrl = r2StorageService.generatePresignedUploadUrl(fileName, contentType);
        String fileId = uploadUrl.substring(
            uploadUrl.lastIndexOf("/") + 1,
            uploadUrl.indexOf("?")
        );
 // Extract key from URL

        return ResponseEntity.ok(Map.of(
            RESPONSE_MESSAGE_KEY, "Upload URL generated",
            "fileId", fileId,
            "fileName", fileName,
            "uploadUrl", uploadUrl
        ));
    }

    @GetMapping(value = "/progress", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamProgress() {
        SseEmitter emitter = new SseEmitter(0L);
        tracker.register(emitter);
        return emitter;
    }

    @GetMapping("/file/download/{fileId}")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<String> downloadFile(@PathVariable("fileId") String fileId) {
        String publicUrl = r2StorageService.generateDownloadUrl(fileId);
        return ResponseEntity.ok(publicUrl);
    }

    @DeleteMapping("/file/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PUBLISHER')")
    public ResponseEntity<Map<String, String>> deleteFile(@PathVariable("id") String fileId) {
        r2StorageService.deleteFile(fileId); // fileId is the object key in R2
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
    @DeleteMapping("/game/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PUBLISHER')")
    public ResponseEntity<Map<String, String>> deleteGameRequest(@PathVariable("id") Long requestId) {
        try {
            requestService.deleteGameRequest(requestId);
            return ResponseEntity.ok(Map.of(RESPONSE_MESSAGE_KEY, "Game request deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(RESPONSE_MESSAGE_KEY, e.getMessage()));
        }
    }

}

