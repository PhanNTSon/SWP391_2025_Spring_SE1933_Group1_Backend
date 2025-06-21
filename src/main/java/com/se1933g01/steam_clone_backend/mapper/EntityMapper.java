package com.se1933g01.steam_clone_backend.mapper;

import com.se1933g01.steam_clone_backend.dto.*;
import com.se1933g01.steam_clone_backend.dto.User.UserDetailDTO;
import com.se1933g01.steam_clone_backend.entity.game.Game;
import com.se1933g01.steam_clone_backend.entity.game.Media;
import com.se1933g01.steam_clone_backend.entity.game.Tag;
import com.se1933g01.steam_clone_backend.entity.user.Publisher;
import com.se1933g01.steam_clone_backend.entity.user.User;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

/**
 * Author: TS Huy
 */
@Component
public class EntityMapper {

    public static TagDTO toTagDTO(Tag tag) {
        if (tag == null) {
            return null;
        }
        return new TagDTO(tag.getTagId(), tag.getTagName());
    }

    // --- Publisher Mapper ---
    public static PublisherBasicDTO toPublisherBasicDTO(Publisher publisher) {
        if (publisher == null)
            return null;
        return new PublisherBasicDTO(publisher.getPublisherId(), publisher.getPublisherName());
    }


    // --- Media Mapper ---
    public static MediaDTO toMediaDTO(Media media) {
        if (media == null)
            return null;
        return new MediaDTO(media.getMediaId(), media.getUrl(), media.getType());
    }

    // --- Game Mappers ---
    public static GameBasicDTO toGameBasicDTO(Game game) {
        if (game == null)
            return null;

        GameBasicDTO dto = new GameBasicDTO();
        dto.setId(game.getGameId());
        dto.setTitle(game.getName());
        if (game.getMedia() != null && !game.getMedia().isEmpty()) {
            Optional<Media> headerImage = game.getMedia().stream()
                    .filter(m -> "image_header".equalsIgnoreCase(m.getType()) ||
                            m.getType().toLowerCase().contains("capsule")) // Tìm kiếm linh hoạt hơn
                    .findFirst();
            if (headerImage.isPresent()) {
                dto.setImageUrl(headerImage.get().getUrl());
            } else {
                game.getMedia().stream().findFirst().ifPresent(m -> dto.setImageUrl(m.getUrl()));
            }
        }
        if (dto.getImageUrl() == null) {
            dto.setImageUrl("https://via.placeholder.com/184x69.png?text=No+Image"); // URL placeholder nếu không có ảnh
        }

        
        // Changed by Phan Son 21-06
        BigDecimal originalPrice = game.getPrice(); // Giá gốc từ entity
        dto.setOriginalPrice(originalPrice);
        BigDecimal discountPrice = originalPrice;
        BigDecimal currentPrice = originalPrice;
        dto.setDiscountPrice(discountPrice);
        dto.setPrice(currentPrice); // price trong DTO là giá bán hiệu lực
        // --!!

        // Added by Phan NT Son 17-06-2025
        dto.setReleaseDate(game.getReleaseDate());

        return dto;
    }

    public static GameDetailDTO toGameDetailDTO(Game game) {
        if (game == null)
            return null;

        GameDetailDTO dto = new GameDetailDTO();
        dto.setGameId(game.getGameId());
        dto.setName(game.getName());
        dto.setReleaseDate(game.getReleaseDate());
        dto.setState(game.getState());
        dto.setPrice(game.getPrice());
        dto.setShortDescription(game.getShortDescription());
        dto.setFullDescription(game.getFullDescription());
        dto.setTotalPurchased(game.getTotalPurchased());
        dto.setOs(game.getOs());
        dto.setStorage(game.getStorage());
        dto.setProcessor(game.getProcessor());
        dto.setMemory(game.getMemory());
        dto.setAdditionalNotes(game.getAdditionalNotes());
        dto.setGraphics(game.getGraphics());

        // Ánh xạ Publisher (quan trọng: xử lý null hoặc lazy loading ở service nếu cần)
        if (game.getPublisher() != null) {
            dto.setPublisher(toPublisherBasicDTO(game.getPublisher()));
        }

        // Ánh xạ Tags
        if (game.getTags() != null && !game.getTags().isEmpty()) {
            dto.setTags(game.getTags().stream().map(EntityMapper::toTagDTO).collect(Collectors.toSet()));
        } else {
            dto.setTags(Collections.emptySet());
        }


        if (game.getMedia() != null && !game.getMedia().isEmpty()) {
            dto.setMedia(game.getMedia().stream().map(EntityMapper::toMediaDTO).collect(Collectors.toList()));
        } else {
            dto.setMedia(Collections.emptyList());
        }

        return dto;
    }

    public static UserDetailDTO toUserDetailDTO(User user) {
        return new UserDetailDTO(
                user.getUserId(),
                user.getEmail(),
                user.getUsername(),
                user.getWalletBalance(),
                user.getAvatarUrl(),
                user.getCountry(),
                user.getDob(),
                user.getGender(),
                user.getProfileName(),
                user.getSummary(),
                user.isBanStatus());
    }
}