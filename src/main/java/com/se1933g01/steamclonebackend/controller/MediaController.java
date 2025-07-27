package com.se1933g01.steamclonebackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.se1933g01.steamclonebackend.dto.MediaDTO;
import com.se1933g01.steamclonebackend.dto.VideoUploadRequestDTO;
import com.se1933g01.steamclonebackend.entity.game.Media;
import com.se1933g01.steamclonebackend.service.MediaService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/games/{gameId}/media") // Base URL cho media của một game cụ thể
public class MediaController {

    @Autowired
    private MediaService mediaService;

    /**
     * Lấy danh sách tất cả media (ảnh, video) của một game.
     * Endpoint này công khai cho tất cả mọi người.
     */
    @GetMapping
    public ResponseEntity<List<MediaDTO>> getMediaForGame(@PathVariable Long gameId) {
        List<MediaDTO> mediaList = mediaService.getMediaForGame(gameId);
        return ResponseEntity.ok(mediaList);
    }

    /**
     * Thêm một video YouTube mới cho game.
     * Chỉ Publisher của game hoặc Admin mới có quyền.
     */
    @PostMapping("/videos")
    // Giả sử bạn có SecurityService để kiểm tra quyền sở hữu game
    @PreAuthorize("@securityService.isGameOwner(#gameId) or hasAuthority('ADMIN')")
    public ResponseEntity<?> addYouTubeVideo(
            @PathVariable Long gameId,
            @RequestBody VideoUploadRequestDTO request) {
        try {
            Media savedMedia = mediaService.addYouTubeVideo(gameId, request.getYoutubeUrl());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new MediaDTO(savedMedia.getMediaId(), savedMedia.getUrl(), savedMedia.getType()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Tải lên một file ảnh mới (screenshot, header,...) cho game.
     * Chỉ Publisher của game hoặc Admin mới có quyền.
     */
    @PostMapping("/images")
    @PreAuthorize("@securityService.isGameOwner(#gameId) or hasAuthority('ADMIN')")
    public ResponseEntity<?> uploadImage(
            @PathVariable Long gameId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String mediaType) { // Client phải gửi kèm type, ví dụ: "screenshot"
        try {
            Media savedMedia = mediaService.uploadImage(gameId, file, mediaType);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new MediaDTO(savedMedia.getMediaId(), savedMedia.getUrl(), savedMedia.getType()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to store image file."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Xóa một media item (ảnh hoặc video).
     * Chỉ Publisher của game hoặc Admin mới có quyền.
     */
    @DeleteMapping("/{mediaId}")
    @PreAuthorize("@securityService.isGameOwner(#gameId) or hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteMedia(
            @PathVariable Long gameId, // gameId vẫn cần để kiểm tra quyền
            @PathVariable Long mediaId) {
        try {
            mediaService.deleteMedia(mediaId);
            return ResponseEntity.noContent().build(); // Trả về 204 No Content khi xóa thành công
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
