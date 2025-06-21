package com.se1933g01.steam_clone_backend.service;

import com.se1933g01.steam_clone_backend.dto.GameBasicDTO;
import com.se1933g01.steam_clone_backend.dto.GameDetailDTO;
import com.se1933g01.steam_clone_backend.entity.game.Game;
import com.se1933g01.steam_clone_backend.mapper.EntityMapper; // Import mapper
import com.se1933g01.steam_clone_backend.repository.GameRepository;
import com.se1933g01.steam_clone_backend.specification.GameSpecification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.EntityNotFoundException; // Hoặc exception tùy chỉnh
import org.hibernate.Hibernate; // Để khởi tạo các collection lazy
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // QUAN TRỌNG cho lazy loading

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author kerri
 */
@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    /**
     * @author kerri
     * @param term
     * @return
     */


    @Transactional(readOnly = true)
    public Page<GameBasicDTO> findGamesByCriteria(String searchTerm, BigDecimal maxPrice, List<Integer> tagIds,
            List<Long> publisherIds, Pageable pageable) {
        Specification<Game> spec = Specification
                .where(GameSpecification.hasSearchTerm(searchTerm)) // <-- THÊM ĐIỀU KIỆN MỚI
                .and(GameSpecification.hasMaxPrice(maxPrice))
                .and(GameSpecification.hasTags(tagIds))
                .and(GameSpecification.hasPublishers(publisherIds));

        Page<Game> gamePage = gameRepository.findAll(spec, pageable);

        return gamePage.map(game -> {
            Hibernate.initialize(game.getMedia());
            return EntityMapper.toGameBasicDTO(game);
        });
    }

    @Transactional(readOnly = true)
    public List<GameBasicDTO> searchGamesByName(String term) {
        if (term == null || term.trim().isEmpty()) {
            return Collections.emptyList();
        }
        Pageable pageable = PageRequest.of(0, 5); // Luôn chỉ lấy 5 gợi ý
        Page<Game> gamePage = gameRepository.findByNameContainingIgnoreCase(term, pageable);
        return gamePage.getContent().stream()
                .map(EntityMapper::toGameBasicDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true) // Đảm bảo session còn mở để load lazy associations
    public List<GameBasicDTO> getAllGamesBasic() {
        return gameRepository.findAll().stream()
                .map(EntityMapper::toGameBasicDTO) // Sử dụng method reference của mapper
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true) // Quan trọng cho việc load các mối quan hệ LAZY
    public GameDetailDTO getGameDetailsById(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found with id: " + gameId));

        Hibernate.initialize(game.getPublisher()); // Nếu publisher là LAZY
        Hibernate.initialize(game.getTags());

        Hibernate.initialize(game.getMedia());

        return EntityMapper.toGameDetailDTO(game);
    }
}
