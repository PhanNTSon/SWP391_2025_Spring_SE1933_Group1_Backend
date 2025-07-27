package com.se1933g01.steamclonebackend.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.se1933g01.steamclonebackend.dto.MediaDTO;
import com.se1933g01.steamclonebackend.entity.game.Game;
import com.se1933g01.steamclonebackend.entity.game.Media;
import com.se1933g01.steamclonebackend.repository.GameRepository;
import com.se1933g01.steamclonebackend.repository.MediaRepo;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MediaService {

    private final MediaRepo mediaRepository;
    private final GameRepository gameRepository;
    private final YouTubeService youTubeService;
    private final R2StorageService r2StorageService;

    public MediaService(MediaRepo mediaRepository, GameRepository gameRepository, YouTubeService youTubeService,
            R2StorageService r2StorageService) {
        this.mediaRepository = mediaRepository;
        this.gameRepository = gameRepository;
        this.youTubeService = youTubeService;
        this.r2StorageService = r2StorageService;
    }

    /**
     * Lấy tất cả media của một game.
     * 
     * @param gameId ID của game.
     * @return Danh sách MediaDTO.
     */
    @Transactional(readOnly = true)
    public List<MediaDTO> getMediaForGame(Long gameId) {
        // Giả sử MediaRepository có phương thức này
        List<Media> mediaList = mediaRepository.findByGame_GameId(gameId);
        return mediaList.stream()
                .map(media -> new MediaDTO(media.getMediaId(), media.getUrl(), media.getType()))
                .collect(Collectors.toList());
    }

    /**
     * Thêm một video YouTube cho game.
     * 
     * @param gameId     ID của game.
     * @param youtubeUrl URL của video YouTube.
     * @return Entity Media đã được lưu.
     */
    @Transactional
    public Media addYouTubeVideo(Long gameId, String youtubeUrl) {
        String videoId = youTubeService.extractVideoIdFromUrl(youtubeUrl);
        if (videoId == null) {
            throw new IllegalArgumentException("Invalid YouTube URL format.");
        }

        if (!youTubeService.isVideoValid(videoId)) {
            throw new IllegalArgumentException("Video is private, does not exist, or cannot be embedded.");
        }

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found with id: " + gameId));

        Media newVideo = new Media();
        newVideo.setGame(game);
        newVideo.setType("video_youtube");
        newVideo.setUrl("https://www.youtube.com/embed/" + videoId);

        return mediaRepository.save(newVideo);
    }

    /**
     * Tải lên một file ảnh cho game.
     * 
     * @param gameId    ID của game.
     * @param file      File ảnh.
     * @param mediaType Loại media (ví dụ: "screenshot", "image_header").
     * @return Entity Media đã được lưu.
     */
    @Transactional
    public Media uploadImage(Long gameId, MultipartFile file, String mediaType) throws IOException {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found with id: " + gameId));
        List<String> uploadResult = r2StorageService.uploadFile(file);

        if (CollectionUtils.isEmpty(uploadResult)) {
            throw new IOException("Failed to upload file to R2 storage: received no key.");
        }        String imageKey = uploadResult.get(0);

        String accessibleImageUrl = r2StorageService.generateDownloadUrl(imageKey);
        Media newImage = new Media();
        newImage.setGame(game);
        newImage.setType(mediaType);
        newImage.setUrl(accessibleImageUrl); // Bây giờ imageUrl là một String, không còn lỗi

        return mediaRepository.save(newImage);
    }

    /**
     * Xóa một media item.
     * 
     * @param mediaId ID của media cần xóa.
     */
    @Transactional
    public void deleteMedia(Long mediaId) {
        // Lấy media để có thể xóa file vật lý nếu cần
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new EntityNotFoundException("Media not found with id: " + mediaId));

        mediaRepository.delete(media);
    }
}
