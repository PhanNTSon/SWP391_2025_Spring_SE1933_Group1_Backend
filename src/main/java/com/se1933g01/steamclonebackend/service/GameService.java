package com.se1933g01.steamclonebackend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.EntityNotFoundException; // Hoặc exception tùy chỉnh
import org.hibernate.Hibernate; // Để khởi tạo các collection lazy
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // QUAN TRỌNG cho lazy loading

import com.se1933g01.steamclonebackend.dto.GameBasicDTO;
import com.se1933g01.steamclonebackend.dto.GameDetailDTO;
import com.se1933g01.steamclonebackend.entity.game.Game;
import com.se1933g01.steamclonebackend.mapper.EntityMapper;
import com.se1933g01.steamclonebackend.repository.GameRepository;
import com.se1933g01.steamclonebackend.specification.GameSpecification;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

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
        Specification<Game> spec = null;

        if (searchTerm != null) {
            spec = GameSpecification.hasSearchTerm(searchTerm);
        }

        if (maxPrice != null) {
            spec = spec == null ? GameSpecification.hasMaxPrice(maxPrice)
                    : spec.and(GameSpecification.hasMaxPrice(maxPrice));
        }

        if (tagIds != null && !tagIds.isEmpty()) {
            spec = spec == null ? GameSpecification.hasTags(tagIds)
                    : spec.and(GameSpecification.hasTags(tagIds));
        }

        if (publisherIds != null && !publisherIds.isEmpty()) {
            spec = spec == null ? GameSpecification.hasPublishers(publisherIds)
                    : spec.and(GameSpecification.hasPublishers(publisherIds));
        }

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
                .toList();
    }

    @Transactional(readOnly = true) // Đảm bảo session còn mở để load lazy associations
    public List<GameBasicDTO> getAllGamesBasic() {
        return gameRepository.findAll().stream()
                .map(EntityMapper::toGameBasicDTO) // Sử dụng method reference của mapper
                .toList();
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
