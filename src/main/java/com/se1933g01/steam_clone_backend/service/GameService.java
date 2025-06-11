package com.se1933g01.steam_clone_backend.service;

import com.se1933g01.steam_clone_backend.dto.GameBasicDTO;
import com.se1933g01.steam_clone_backend.dto.GameDetailDTO;
import com.se1933g01.steam_clone_backend.entity.game.Game;
import com.se1933g01.steam_clone_backend.mapper.EntityMapper; // Import mapper
import com.se1933g01.steam_clone_backend.repository.GameRepository;
import com.se1933g01.steam_clone_backend.specification.GameSpecification;

import jakarta.persistence.EntityNotFoundException; // Hoặc exception tùy chỉnh
import org.hibernate.Hibernate; // Để khởi tạo các collection lazy
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // QUAN TRỌNG cho lazy loading

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameService {

    private final GameRepository gameRepository;

    @Autowired
    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Transactional(readOnly = true) // Quan trọng để xử lý lazy loading khi mapping sang DTO
    public List<GameBasicDTO> findGamesByCriteria(Double maxPrice, List<Integer> tagIds, List<Long> publisherIds) {
        Specification<Game> spec = Specification
                .where(GameSpecification.hasMaxPrice(maxPrice))
                .and(GameSpecification.hasTags(tagIds))
                .and(GameSpecification.hasPublishers(publisherIds));

        // Framework sẽ tự động bỏ qua các điều kiện null.

        // 2. Gọi repository với Specification đã tạo
        List<Game> games = gameRepository.findAll(spec);

        // 3. Chuyển đổi sang DTO (và xử lý lazy loading nếu cần)
        return games.stream()
                .map(game -> {
                    Hibernate.initialize(game.getMedia());
                    return EntityMapper.toGameBasicDTO(game);
                })
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

    // ... các service method khác (search, create, update game cũng nên dùng DTO)
    // Ví dụ: khi tạo game, bạn có thể nhận GameInputDTO và chuyển thành Entity Game
}
