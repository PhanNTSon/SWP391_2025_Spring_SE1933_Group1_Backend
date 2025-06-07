package com.se1933g01.steam_clone_backend.mapper;

import com.se1933g01.steam_clone_backend.dto.*;
import com.se1933g01.steam_clone_backend.entity.game.Game;
import com.se1933g01.steam_clone_backend.entity.game.Media;
import com.se1933g01.steam_clone_backend.entity.game.Tag;
import com.se1933g01.steam_clone_backend.entity.game.SystemRequirement;
import com.se1933g01.steam_clone_backend.entity.user.Publisher;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Author: TS Huy
 */
public class EntityMapper {

    public static TagDTO toTagDTO(Tag tag) {
        if (tag == null)
            return null;
        return new TagDTO(tag.getTagId(), tag.getTagName());
    }

    // --- Publisher Mapper ---
    public static PublisherBasicDTO toPublisherBasicDTO(Publisher publisher) {
        if (publisher == null)
            return null;
        // Giả sử Publisher entity có getPublisherId() và getPublisherName()
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

        // 2. Xử lý giá và giảm giá
        double originalPrice = game.getPrice(); // Giá gốc từ entity
        dto.setOriginalPrice(originalPrice);
<<<<<<< Updated upstream

        // *** LOGIC GIẢ LẬP GIẢM GIÁ CHO VÍ DỤ ***
        // Trong ứng dụng thực tế, logic này sẽ phức tạp hơn, dựa trên promotion, sale
        // event, etc.
        double discountPrice = originalPrice; // Mặc định không giảm
        double currentPrice = originalPrice; // Giá bán hiện tại

        // if (originalPrice != null && originalPrice.compareTo(double.valueOf(20)) > 0) { // Ví dụ: giảm 10% nếu giá >
        //                                                                                     // 20
        //     double discountAmount = originalPrice.multiply(double.valueOf(0.10)) // Giảm 10%
        //             .setScale(2, RoundingMode.HALF_UP);
        //     discountPrice = originalPrice.subtract(discountAmount);
        //     currentPrice = discountPrice;
        // } else if (originalPrice != null && originalPrice.compareTo(double.ZERO) == 0) {
        //     // Nếu game miễn phí
        //     discountPrice = double.ZERO;
        //     currentPrice = double.ZERO;
        // }

=======
        double discountPrice = originalPrice;
        double currentPrice = originalPrice; 
>>>>>>> Stashed changes
        dto.setDiscountPrice(discountPrice);
        dto.setPrice(currentPrice); // price trong DTO là giá bán hiệu lực

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
}