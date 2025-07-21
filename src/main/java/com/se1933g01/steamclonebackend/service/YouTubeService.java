package com.se1933g01.steamclonebackend.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class YouTubeService {

    @Value("${youtube.api.key}")
    private String apiKey;

    private static final String APPLICATION_NAME = "Steam Clone Backend";

    /**
     * Trích xuất Video ID từ các định dạng URL YouTube khác nhau.
     */
    public String extractVideoIdFromUrl(String url) {
        String videoId = null;
        // Mẫu regex để tìm videoId trong các link youtube.com và youtu.be
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);
        if (matcher.find()) {
            videoId = matcher.group();
        }
        return videoId;
    }

    /**
     * Kiểm tra xem một video YouTube có tồn tại, công khai và cho phép nhúng không.
     * @return true nếu hợp lệ, false nếu không.
     */
    public boolean isVideoValid(String videoId) {
        try {
            YouTube youtube = new YouTube.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(),
                    null)
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            YouTube.Videos.List request = youtube.videos()
                    .list("status") // Chỉ yêu cầu thông tin về 'status'
                    .setId(videoId)
                    .setKey(apiKey);

            VideoListResponse response = request.execute();
            if (response.getItems().isEmpty()) {
                return false; // Video không tồn tại
            }

            Video video = response.getItems().get(0);
            // Kiểm tra xem video có công khai (public) và cho phép nhúng (embeddable) không
            return "public".equals(video.getStatus().getPrivacyStatus()) && Boolean.TRUE.equals(video.getStatus().getEmbeddable());

        } catch (Exception e) {
            // Log lỗi trong thực tế
            e.printStackTrace();
            return false;
        }
    }
}

