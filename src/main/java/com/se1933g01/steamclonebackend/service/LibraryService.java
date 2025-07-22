package com.se1933g01.steamclonebackend.service;

import com.se1933g01.steamclonebackend.dto.GameDetailDTO;
import com.se1933g01.steamclonebackend.dto.MediaDTO;
import com.se1933g01.steamclonebackend.dto.PublisherBasicDTO;
import com.se1933g01.steamclonebackend.dto.TagDTO;
import com.se1933g01.steamclonebackend.dto.user.LibraryGameDTO;
import com.se1933g01.steamclonebackend.entity.user.Library;
import com.se1933g01.steamclonebackend.entity.game.Game;
import com.se1933g01.steamclonebackend.repository.LibraryRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class LibraryService {

    private final LibraryRepository libraryRepository;

    @Autowired
    public LibraryService(LibraryRepository libraryRepository) {
        this.libraryRepository = libraryRepository;
    }

    /**
     * Lấy thư viện game của một người dùng, có hỗ trợ phân trang và sắp xếp.
     *
     * @param userId   ID của người dùng.
     * @param pageable Đối tượng phân trang và sắp xếp.
     * @return một đối tượng Page chứa các LibraryGameDTO.
     */
    @Transactional(readOnly = true)
    public Page<LibraryGameDTO> showLibrary(Long userId, Pageable pageable) {
        // 1. Gọi repository để lấy một trang các bản ghi Library
        Page<Library> libraryPage = libraryRepository.findByIdUserId(userId, pageable);

        // 2. Sử dụng hàm map của Page để chuyển đổi mỗi Library entity thành một
        // LibraryGameDTO
        return libraryPage.map(this::mapLibraryEntryToDto);
    }

    /**
     * Hàm helper để chuyển đổi một entity Library thành một LibraryGameDTO.
     * Phiên bản này sẽ tạo một GameDetailDTO hoàn chỉnh theo yêu cầu.
     */
    public LibraryGameDTO mapLibraryEntryToDto(Library libraryEntry) {
        Game gameEntity = libraryEntry.getGame();

        GameDetailDTO gameDetail = new GameDetailDTO();
        gameDetail.setGameId(gameEntity.getGameId());

        if (gameEntity.getPublisher() != null) {
            gameDetail.setPublisher(new PublisherBasicDTO(gameEntity.getPublisher().getPublisherId(),
                    gameEntity.getPublisher().getPublisherName(), gameEntity.getPublisher().getImageUrl()));
        }

        gameDetail.setName(gameEntity.getName());
        gameDetail.setReleaseDate(gameEntity.getReleaseDate());
        gameDetail.setState(gameEntity.getState());
        gameDetail.setPrice(gameEntity.getPrice());
        gameDetail.setShortDescription(gameEntity.getShortDescription());
        gameDetail.setFullDescription(gameEntity.getFullDescription());
        gameDetail.setTotalPurchased(gameEntity.getTotalPurchased());

        if (gameEntity.getTags() != null) {
            gameDetail.setTags(gameEntity.getTags().stream()
                    .map(tag -> new TagDTO(tag.getTagId(), tag.getTagName()))
                    .collect(Collectors.toSet()));
        }

        if (gameEntity.getMedia() != null) {
            gameDetail.setMedia(gameEntity.getMedia().stream()
                    .map(media -> new MediaDTO(media.getMediaId(), media.getUrl(), media.getType()))
                    .toList());
        }

        gameDetail.setOs(gameEntity.getOs());
        gameDetail.setStorage(gameEntity.getStorage());
        gameDetail.setProcessor(gameEntity.getProcessor());
        gameDetail.setMemory(gameEntity.getMemory());
        gameDetail.setAdditionalNotes(gameEntity.getAdditionalNotes());
        gameDetail.setGraphics(gameEntity.getGraphics());
        gameDetail.setGameUrl(gameEntity.getGameUrl());
        gameDetail.setIconUrl(gameEntity.getIconUrl());

        gameDetail.setGameUrl(gameEntity.getGameUrl());

        // Bước B: Tạo DTO chính cho game trong thư viện
        LibraryGameDTO libraryGameDTO = new LibraryGameDTO();
        libraryGameDTO.setGameDetail(gameDetail); // Gán đối tượng GameDetailDTO vừa tạo

        // Chuyển đổi LocalDate thành LocalDateTime (nếu cần)
        if (libraryEntry.getDateAdded() != null) {
            libraryGameDTO.setDateAdded(libraryEntry.getDateAdded());
        }

        libraryGameDTO.setPlaytimeInMillis(libraryEntry.getPlaytimeInMillis());

        return libraryGameDTO;
    }

    /**
     * Lấy thông tin chi tiết của một game cụ thể trong thư viện của người dùng.
     * 
     * @param userId ID của người dùng.
     * @param gameId ID của game.
     * @return một đối tượng LibraryGameDTO.
     * @throws EntityNotFoundException nếu người dùng không sở hữu game này.
     */
    @Transactional(readOnly = true)
    public LibraryGameDTO getGameInLibrary(Long userId, Long gameId) {
        Library libraryEntry = libraryRepository.findById_UserIdAndId_GameId(userId, gameId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found game " + gameId + " in library of user " + userId));
        return this.mapLibraryEntryToDto(libraryEntry);
    }

    @Transactional
    public void updatePlaytime(Long userId, Long gameId, Long PlaytimeInMillis) {
        Library libraryEntry = libraryRepository.findById_UserIdAndId_GameId(userId, gameId)
                .orElseThrow(() -> new EntityNotFoundException("Not found game " + gameId + " in library of user "));
        libraryEntry.setPlaytimeInMillis(PlaytimeInMillis);

        libraryRepository.save(libraryEntry);
    }
}
