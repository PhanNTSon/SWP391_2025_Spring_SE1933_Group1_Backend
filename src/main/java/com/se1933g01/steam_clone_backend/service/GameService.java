package com.se1933g01.steam_clone_backend.service;

import com.se1933g01.steam_clone_backend.dto.GameBasicDTO;
import com.se1933g01.steam_clone_backend.dto.GameDetailDTO;
import com.se1933g01.steam_clone_backend.entity.Game;
import com.se1933g01.steam_clone_backend.mapper.EntityMapper; // Import mapper
import com.se1933g01.steam_clone_backend.repository.GameRepository;

import jakarta.persistence.EntityNotFoundException; // Hoặc exception tùy chỉnh
import org.hibernate.Hibernate; // Để khởi tạo các collection lazy
import org.springframework.beans.factory.annotation.Autowired;
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

    @Transactional(readOnly = true) // Đảm bảo session còn mở để load lazy associations
    public List<GameBasicDTO> getAllGamesBasic() {
        return gameRepository.findAll().stream()
                .map(EntityMapper::toGameBasicDTO) // Sử dụng method reference của mapper
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true) // Quan trọng cho việc load các mối quan hệ LAZY
    public GameDetailDTO getGameDetailsById(Integer gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found with id: " + gameId));

        // Chủ động khởi tạo các collection/entity LAZY nếu cần trước khi session đóng
        // Điều này rất quan trọng nếu các mối quan hệ là LAZY
        Hibernate.initialize(game.getPublisher()); // Nếu publisher là LAZY
        Hibernate.initialize(game.getTags());
        // Hibernate.initialize(game.getSystemRequirement());
        // if (game.getSystemRequirement() != null) {
        //     // Nếu SystemRequirement cũng có các trường LAZY cần hiển thị, bạn cũng cần
        //     // initialize chúng
        //     // Hoặc đảm bảo rằng toSystemRequirementDTO không truy cập trường LAZY đó
        // }
        Hibernate.initialize(game.getMedia());

        return EntityMapper.toGameDetailDTO(game);
    }

    // ... các service method khác (search, create, update game cũng nên dùng DTO)
    // Ví dụ: khi tạo game, bạn có thể nhận GameInputDTO và chuyển thành Entity Game
}
