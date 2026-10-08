package com.se1933g01.steamclonebackend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.ext.tables.TablesExtension;


import jakarta.persistence.EntityNotFoundException; // Hoặc exception tùy chỉnh
import org.hibernate.Hibernate; // Để khởi tạo các collection lazy
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // QUAN TRỌNG cho lazy loading

import com.se1933g01.steamclonebackend.dto.AddingGameRequestDTO;
import com.se1933g01.steamclonebackend.dto.GameBasicDTO;
import com.se1933g01.steamclonebackend.dto.GameDetailDTO;
import com.se1933g01.steamclonebackend.dto.GamePresentDTO;
import com.se1933g01.steamclonebackend.dto.MediaDTO;
import com.se1933g01.steamclonebackend.dto.NewsDTO;
import com.se1933g01.steamclonebackend.dto.TagDTO;
import com.se1933g01.steamclonebackend.entity.game.Game;
import com.se1933g01.steamclonebackend.entity.game.Tag;
import com.se1933g01.steamclonebackend.entity.news.News;
import com.se1933g01.steamclonebackend.entity.request.AddingGameRequest;
import com.se1933g01.steamclonebackend.entity.user.Library;
import com.se1933g01.steamclonebackend.entity.user.Publisher;
import com.se1933g01.steamclonebackend.mapper.EntityMapper;
import com.se1933g01.steamclonebackend.repository.AddingGameRequestRepo;
import com.se1933g01.steamclonebackend.repository.GameRepository;
import com.se1933g01.steamclonebackend.repository.LibraryRepository;
import com.se1933g01.steamclonebackend.repository.NewsRepo;
import com.se1933g01.steamclonebackend.specification.GameSpecification;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
/**
 * @author kerri
 */
@Service
public class GameService {

    private final GameRepository gameRepository;
    private final ModelMapper modelMapper;
    private final AddingGameRequestRepo addingGameRequestRepo;
    private final LibraryRepository libraryRepo;
    private final NewsRepo newsRepo;
    // private final Parser parser;
    // private final HtmlRenderer renderer;
    public GameService(GameRepository gameRepository, ModelMapper modelMapper, AddingGameRequestRepo addingGameRequestRepo, LibraryRepository libraryRepo, NewsRepo newsRepo, Parser parser, HtmlRenderer renderer) {
        this.newsRepo = newsRepo;
        this.libraryRepo = libraryRepo;
        this.addingGameRequestRepo = addingGameRequestRepo;
        this.gameRepository = gameRepository;
        this.modelMapper = modelMapper;
        // this.parser = parser;
        // this.renderer = renderer;
    }

    public List<GamePresentDTO> getGamesUnder5() {
        PageRequest top20 = PageRequest.of(0, 20);
        List<Game> cheapGames = gameRepository.findTop10PriceUnder5(top20);
        return cheapGames.stream().map(game -> new GamePresentDTO(
                game.getGameId(),
                game.getName(),
                game.getMedia().stream().map(media -> new MediaDTO(
                        media.getMediaId(),
                        media.getUrl(),
                        media.getType())).toList(),
                game.getShortDescription(),
                game.getPrice(),
                game.getTags().stream().map(tag -> new TagDTO(
                        tag.getTagId(),
                        tag.getTagName())).toList()))
                .toList();
    }

    public List<GamePresentDTO> getNewPublishGames() {
        PageRequest top20 = PageRequest.of(0, 20);
        List<Game> newGames = gameRepository.findTop10NewPublish(LocalDate.now().minusDays(30), top20);
        return newGames.stream().map(game -> new GamePresentDTO(
                game.getGameId(),
                game.getName(),
                game.getMedia().stream().map(media -> new MediaDTO(
                        media.getMediaId(),
                        media.getUrl(),
                        media.getType())).toList(),
                game.getShortDescription(),
                game.getPrice(),
                game.getTags().stream().map(tag -> new TagDTO(
                        tag.getTagId(),
                        tag.getTagName())).toList()))
                .toList();
    }

    public List<GamePresentDTO> getTopSellingGames() {
        PageRequest top10 = PageRequest.of(0, 10);
        List<Game> topSelling = gameRepository.findTop10TopSelling(top10);
        return topSelling.stream().map(game -> new GamePresentDTO(
                game.getGameId(),
                game.getName(),
                game.getMedia().stream().map(media -> new MediaDTO(
                        media.getMediaId(),
                        media.getUrl(),
                        media.getType())).toList(),
                game.getShortDescription(),
                game.getPrice(),
                game.getTags().stream().map(tag -> new TagDTO(
                        tag.getTagId(),
                        tag.getTagName())).toList()))
                .toList();
    }

    public List<GamePresentDTO> getMostRatedGames() {
        PageRequest top16 = PageRequest.of(0, 16);
        List<Game> topSelling = gameRepository.findTop16MostRecommended(top16);
        return topSelling.stream().map(game -> new GamePresentDTO(
                game.getGameId(),
                game.getName(),
                game.getMedia().stream().map(media -> new MediaDTO(
                        media.getMediaId(),
                        media.getUrl(),
                        media.getType())).toList(),
                game.getShortDescription(),
                game.getPrice(),
                game.getTags().stream().map(tag -> new TagDTO(
                        tag.getTagId(),
                        tag.getTagName())).toList()))
                .toList();
    }

    /**
     * @author kerri
     * @param term
     * @return
     */

    @Transactional(readOnly = true)
    public Page<GameBasicDTO> findGamesByCriteria(String searchTerm, BigDecimal maxPrice, List<Integer> tagIds,
            List<Long> publisherIds, Pageable pageable, Boolean state) {
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

        if (state!= null) {
            spec = spec == null? GameSpecification.hasStateTrue(state)
                    : spec.and(GameSpecification.hasStateTrue(state));
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
        Pageable pageable = PageRequest.of(0, 5); 
        Page<Game> gamePage = gameRepository.findByNameContainingIgnoreCase(term, pageable);
        return gamePage.getContent().stream()
                .map(EntityMapper::toGameBasicDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GameBasicDTO> getAllGamesBasic() {
        return gameRepository.findAll().stream()
                .map(EntityMapper::toGameBasicDTO)
                .toList();
    }

    @Transactional(readOnly = true) // Quan trọng cho việc load các mối quan hệ LAZY
    public GameDetailDTO getGameDetailsById(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found with id: " + gameId));
        if(!game.getState()) {
            throw new EntityNotFoundException("Game is not available.");
        }

        Hibernate.initialize(game.getPublisher()); // Nếu publisher là LAZY
        Hibernate.initialize(game.getTags());

        Hibernate.initialize(game.getMedia());

        return EntityMapper.toGameDetailDTO(game);
    }

    public Page<GameBasicDTO> getGamesByPublisherApproved(Publisher publisher, String name, Pageable pageable) {
        Page<Game> gamePage;

        if (name != null && !name.trim().isEmpty()) {
            gamePage = gameRepository.findByPublisherAndNameContainingIgnoreCase(publisher, name, pageable);
        } else {
            gamePage = gameRepository.findByPublisher(publisher, pageable);
        }

        return gamePage.map(game -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            GameBasicDTO dto = modelMapper.map(game, GameBasicDTO.class);
            dto.setTitle(game.getName());
            dto.setImageUrl(game.getMedia().get(0).getUrl());
            dto.setId(game.getGameId());
            dto.setState(game.getState());
            return dto;
        });
    }

    public Page<AddingGameRequestDTO> getPendingRequestsByPublisher(Long publisherId, String name, Pageable pageable) {
        Page<AddingGameRequest> requestPage;

        if (name != null && !name.trim().isEmpty()) {
            requestPage = addingGameRequestRepo.findByPublisherAndGameNameContainingIgnoreCase(publisherId, name,
                    pageable);
        } else {
            requestPage = addingGameRequestRepo.findPendingGamesByPublisher(publisherId, pageable);
        }

        return requestPage.map(request -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            AddingGameRequestDTO dto = modelMapper.map(request, AddingGameRequestDTO.class);
            dto.setSendDate(request.getRequest().getTimeCreated().toString());
            return dto;
        });
    }

    public Page<AddingGameRequestDTO> getDeclinedRequestsByPublisher(Long publisherId, String name, Pageable pageable) {
        Page<AddingGameRequest> requestPage;

        if (name != null && !name.trim().isEmpty()) {
            requestPage = addingGameRequestRepo.findDeclinedGamesByPublisherAndGameNameContainingIgnoreCase(publisherId,
                    name, pageable);
        } else {
            requestPage = addingGameRequestRepo.findDeclinedGamesByPublisher(publisherId, pageable);
        }

        return requestPage.map(request -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            AddingGameRequestDTO dto = modelMapper.map(request, AddingGameRequestDTO.class);
            dto.setSendDate(request.getRequest().getTimeCreated().toString());
            return dto;
        });
    }

    public void hideGame(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found with id: " + gameId));
        game.setState(false);
        gameRepository.save(game);
    }

    public void unhideGame(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game not found with id: " + gameId));
        game.setState(true);
        gameRepository.save(game);
    }

    public List<GameBasicDTO> getTagBasedRecommendations(Long gameId) {
        // Step 1: Get related game IDs based on tag overlap
        List<Long> relatedGameIds = gameRepository.findRelatedGameIdsByTag(gameId);

        // Step 2: Fetch full game entities
        List<Game> games = gameRepository.findAllById(relatedGameIds);

        // Step 3: Map to DTO including tags
        return games.stream()
                .filter(game -> Boolean.TRUE.equals(game.getState()))
                .map(game -> {
                    String imageUrl = game.getMedia().isEmpty() ? null : game.getMedia().get(0).getUrl();
                    Set<String> tagNames = game.getTags().stream()
                            .map(Tag::getTagName)
                            .collect(Collectors.toSet());

                return new GameBasicDTO(
                    game.getGameId(),
                    game.getName(),
                    imageUrl,
                    game.getPrice(),
                    null,                      
                    game.getPrice(),          
                    true,     
                    null,                
                    game.getReleaseDate(),
                    tagNames                   
                );
            })
            .collect(Collectors.toList());
    }

    public List<GameBasicDTO> recommendSimilarToLibrary(Long userId) {
        List<Library> library = libraryRepo.findByUser_UserId(userId);
        Set<Long> ownedGameIds = library.stream()
                .map(lib -> lib.getGame().getGameId())
                .collect(Collectors.toSet());
        Set<Tag> preferredTags = library.stream()
                .flatMap(lib -> lib.getGame().getTags().stream())
                .collect(Collectors.toSet());
        List<Game> candidates = gameRepository.findDistinctByTagsInAndGameIdNotInAndStateTrue(preferredTags,
                ownedGameIds);
        Map<Game, Long> similarityMap = candidates.stream()
                .collect(Collectors.toMap(
                        game -> game,
                        game -> game.getTags().stream()
                                .filter(preferredTags::contains)
                                .count()));
        return similarityMap.entrySet().stream()
                .sorted(Map.Entry.<Game, Long>comparingByValue().reversed())
                .limit(10)
                .map(entry -> {
                    Game game = entry.getKey();
                    String imageUrl = game.getMedia().isEmpty() ? null : game.getMedia().get(0).getUrl();
                    Set<String> tagNames = game.getTags().stream()
                            .map(Tag::getTagName)
                            .collect(Collectors.toSet());

                return new GameBasicDTO(
                    game.getGameId(),
                    game.getName(),
                    imageUrl,
                    game.getPrice(),
                    null,                           
                    game.getPrice(),             
                    Boolean.TRUE.equals(game.getState()),
                    null,
                    game.getReleaseDate(),
                    tagNames                 
                );
            })
            .toList();
    }
    public Page<NewsDTO> getPagedNewsByGameId(Long gameId, int page, int size) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return newsRepo.findByGame_GameId(gameId, pageable)
            .map(news -> modelMapper.map(news, NewsDTO.class));
    }

    public NewsDTO createNews(Long gameId, NewsDTO newsDTO) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        Game game = gameRepository.findById(gameId)
            .orElseThrow(() -> new RuntimeException("Game not found"));

        News news = modelMapper.map(newsDTO, News.class);
        news.setGame(game); // link the news to the game
        news.setCreatedAt(LocalDate.now());
        News saved = newsRepo.save(news);
        return modelMapper.map(saved, NewsDTO.class);
    }

    public NewsDTO getNewsDetails(Long id) {
        News news = newsRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("News not found"));

        // ✅ Flexmark setup with table support
        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create()));

        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        Node document = parser.parse(news.getMarkdown());
        String html = renderer.render(document);

        // ✅ Map News to DTO
        NewsDTO dto = modelMapper.map(news, NewsDTO.class);
        dto.setHtmlContent(html);

        return dto;
    }



    public News updateNews(Long newsId, NewsDTO dto) {
        News news = newsRepo.findById(newsId)
                .orElseThrow(() -> new RuntimeException("News not found"));
        news.setTitle(dto.getTitle());
        news.setSummary(dto.getSummary());
        news.setMarkdown(dto.getMarkdown());
        return newsRepo.save(news);
    }
    public void deleteNewsById(Long id) {
        News news = newsRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("News not found"));
        newsRepo.delete(news);
    }
    public Page<GameBasicDTO> getListedGame(String name, Pageable pageable) {
        Page<Game> gamePage;

        if (name != null && !name.trim().isEmpty()) {
            gamePage = gameRepository.findByNameContainingIgnoreCaseAndStateTrue(name, pageable);
        } else {
            gamePage = gameRepository.findByStateTrue(pageable);
        }

        return gamePage.map(game -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            GameBasicDTO dto = modelMapper.map(game, GameBasicDTO.class);
            dto.setTitle(game.getName());
            dto.setImageUrl(game.getMedia().get(0).getUrl());
            dto.setId(game.getGameId());
            dto.setState(game.getState());
            dto.setPublisherId(game.getPublisher().getPublisherId());
            return dto;
        });
    }

    public Page<GameBasicDTO> getHiddenGame(String name, Pageable pageable) {
        Page<Game> gamePage;

        if (name != null && !name.trim().isEmpty()) {
            gamePage = gameRepository.findByNameContainingIgnoreCaseAndStateFalse(name, pageable);
        } else {
            gamePage = gameRepository.findByStateFalse(pageable);
        }

        return gamePage.map(game -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            GameBasicDTO dto = modelMapper.map(game, GameBasicDTO.class);
            dto.setTitle(game.getName());
            dto.setImageUrl(game.getMedia().get(0).getUrl());
            dto.setId(game.getGameId());
            dto.setState(game.getState());
            dto.setPublisherId(game.getPublisher().getPublisherId());
            return dto;
        });
    }
    
}
