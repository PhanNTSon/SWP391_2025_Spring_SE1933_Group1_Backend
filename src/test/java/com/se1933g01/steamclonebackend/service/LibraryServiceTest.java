package com.se1933g01.steamclonebackend.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.se1933g01.steamclonebackend.dto.user.LibraryGameDTO;
import com.se1933g01.steamclonebackend.entity.game.Game;
import com.se1933g01.steamclonebackend.entity.game.Media;
import com.se1933g01.steamclonebackend.entity.user.Publisher;
import com.se1933g01.steamclonebackend.entity.game.Tag;
import com.se1933g01.steamclonebackend.entity.user.Library;
import com.se1933g01.steamclonebackend.entity.user.LibraryId;
import com.se1933g01.steamclonebackend.entity.user.User;
import com.se1933g01.steamclonebackend.repository.LibraryRepository;

public class LibraryServiceTest {

    private LibraryRepository libraryRepository;
    private LibraryService libraryService;

    @Before
    public void setUp() {
        libraryRepository = mock(LibraryRepository.class);
        libraryService = new LibraryService(libraryRepository);
    }

    // ==================== showLibrary Tests ====================

    @Test
    public void showLibrary_validUserIdWithGames_returnsPagedLibraryGameDTO() {
        // Arrange
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        // Create Publisher
        Publisher publisher = new Publisher();
        publisher.setPublisherId(1L);
        publisher.setPublisherName("Test Publisher");
        publisher.setImageUrl("http://example.com/publisher.jpg");

        // Create Tags
        Tag tag1 = new Tag();
        tag1.setTagId(1);
        tag1.setTagName("Action");

        Tag tag2 = new Tag();
        tag2.setTagId(2);
        tag2.setTagName("Adventure");

        Set<Tag> tags = new HashSet<>();
        tags.add(tag1);
        tags.add(tag2);

        // Create Media
        Media media1 = new Media();
        media1.setMediaId(1L);
        media1.setUrl("http://example.com/image1.jpg");
        media1.setType("image");

        Media media2 = new Media();
        media2.setMediaId(2L);
        media2.setUrl("http://example.com/image2.jpg");
        media2.setType("video");

        List<Media> mediaList = new ArrayList<>();
        mediaList.add(media1);
        mediaList.add(media2);

        // Create Game
        Game game = new Game();
        game.setGameId(100L);
        game.setName("Test Game");
        game.setPublisher(publisher);
        game.setReleaseDate(LocalDate.of(2023, 5, 15));
        game.setState(true); // Fixed: Boolean instead of String
        game.setPrice(new BigDecimal("29.99"));
        game.setShortDescription("Short description");
        game.setFullDescription("Full description");
        game.setTotalPurchased(1000);
        game.setTags(tags);
        game.setMedia(mediaList);
        game.setOs("Windows");
        game.setStorage("50 GB");
        game.setProcessor("Intel i5");
        game.setMemory("8 GB RAM");
        game.setAdditionalNotes("Additional notes");
        game.setGraphics("GTX 1060");

        // Create User
        User user = new User();
        user.setUserId(userId);

        // Create Library
        LibraryId libraryId = new LibraryId();
        libraryId.setUserId(userId);
        libraryId.setGameId(100L);

        Library library = new Library();
        library.setId(libraryId);
        library.setUser(user);
        library.setGame(game);
        library.setDateAdded(LocalDateTime.of(2023, 6, 1, 10, 30));
        library.setPlaytimeInMillis(3600000L); // 1 hour

        List<Library> libraryList = new ArrayList<>();
        libraryList.add(library);

        Page<Library> libraryPage = new PageImpl<>(libraryList, pageable, 1);

        when(libraryRepository.findByIdUserId(userId, pageable)).thenReturn(libraryPage);

        // Act
        Page<LibraryGameDTO> result = libraryService.showLibrary(userId, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());

        LibraryGameDTO libraryGameDTO = result.getContent().get(0);
        assertNotNull(libraryGameDTO);
        assertNotNull(libraryGameDTO.getGameDetail());

        // Verify game details
        assertEquals(100L, libraryGameDTO.getGameDetail().getGameId());
        assertEquals("Test Game", libraryGameDTO.getGameDetail().getName());
        assertEquals(new BigDecimal("29.99"), libraryGameDTO.getGameDetail().getPrice());
        assertEquals("Short description", libraryGameDTO.getGameDetail().getShortDescription());
        assertEquals("Full description", libraryGameDTO.getGameDetail().getFullDescription());
        assertEquals(Integer.valueOf(1000), libraryGameDTO.getGameDetail().getTotalPurchased());
        assertEquals(Boolean.TRUE, libraryGameDTO.getGameDetail().getState()); // Fixed: Boolean comparison
        assertEquals(LocalDate.of(2023, 5, 15), libraryGameDTO.getGameDetail().getReleaseDate());

        // Verify publisher
        assertNotNull(libraryGameDTO.getGameDetail().getPublisher());
        assertEquals(Long.valueOf(1L), libraryGameDTO.getGameDetail().getPublisher().getPublisherId());
        assertEquals("Test Publisher", libraryGameDTO.getGameDetail().getPublisher().getPublisherName());
        assertEquals("http://example.com/publisher.jpg", libraryGameDTO.getGameDetail().getPublisher().getImageUrl());

        // Verify tags
        assertNotNull(libraryGameDTO.getGameDetail().getTags());
        assertEquals(2, libraryGameDTO.getGameDetail().getTags().size());

        // Verify media
        assertNotNull(libraryGameDTO.getGameDetail().getMedia());
        assertEquals(2, libraryGameDTO.getGameDetail().getMedia().size());

        // Verify system requirements
        assertEquals("Windows", libraryGameDTO.getGameDetail().getOs());
        assertEquals("50 GB", libraryGameDTO.getGameDetail().getStorage());
        assertEquals("Intel i5", libraryGameDTO.getGameDetail().getProcessor());
        assertEquals("8 GB RAM", libraryGameDTO.getGameDetail().getMemory());
        assertEquals("Additional notes", libraryGameDTO.getGameDetail().getAdditionalNotes());
        assertEquals("GTX 1060", libraryGameDTO.getGameDetail().getGraphics());

        // Verify library specific fields
        assertEquals(LocalDateTime.of(2023, 6, 1, 10, 30), libraryGameDTO.getDateAdded());
        assertEquals(Long.valueOf(3600000L), libraryGameDTO.getPlaytimeInMillis());

        verify(libraryRepository).findByIdUserId(userId, pageable);
    }

    @Test
    public void showLibrary_validUserIdWithNoGames_returnsEmptyPage() {
        // Arrange
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        List<Library> emptyLibraryList = new ArrayList<>();
        Page<Library> emptyLibraryPage = new PageImpl<>(emptyLibraryList, pageable, 0);

        when(libraryRepository.findByIdUserId(userId, pageable)).thenReturn(emptyLibraryPage);

        // Act
        Page<LibraryGameDTO> result = libraryService.showLibrary(userId, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getContent().size());
        assertEquals(0, result.getTotalElements());
        assertTrue(result.isEmpty());

        verify(libraryRepository).findByIdUserId(userId, pageable);
    }

    @Test
    public void showLibrary_multipleGamesWithPagination_returnsCorrectPage() {
        // Arrange
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 2); // Page 0, size 2

        // Create first game
        Game game1 = new Game();
        game1.setGameId(100L);
        game1.setName("Game 1");
        game1.setPrice(new BigDecimal("19.99"));

        // Create second game
        Game game2 = new Game();
        game2.setGameId(101L);
        game2.setName("Game 2");
        game2.setPrice(new BigDecimal("29.99"));

        // Create User
        User user = new User();
        user.setUserId(userId);

        // Create Library entries
        LibraryId libraryId1 = new LibraryId();
        libraryId1.setUserId(userId);
        libraryId1.setGameId(100L);

        Library library1 = new Library();
        library1.setId(libraryId1);
        library1.setUser(user);
        library1.setGame(game1);
        library1.setDateAdded(LocalDateTime.now());

        LibraryId libraryId2 = new LibraryId();
        libraryId2.setUserId(userId);
        libraryId2.setGameId(101L);

        Library library2 = new Library();
        library2.setId(libraryId2);
        library2.setUser(user);
        library2.setGame(game2);
        library2.setDateAdded(LocalDateTime.now());

        List<Library> libraryList = new ArrayList<>();
        libraryList.add(library1);
        libraryList.add(library2);

        Page<Library> libraryPage = new PageImpl<>(libraryList, pageable, 5); // Total 5 elements

        when(libraryRepository.findByIdUserId(userId, pageable)).thenReturn(libraryPage);

        // Act
        Page<LibraryGameDTO> result = libraryService.showLibrary(userId, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(5, result.getTotalElements());
        assertEquals(0, result.getNumber());
        assertEquals(2, result.getSize());

        verify(libraryRepository).findByIdUserId(userId, pageable);
    }

    // ==================== mapLibraryEntryToDto Tests ====================

    @Test
    public void mapLibraryEntryToDto_completeLibraryEntry_returnsCompleteDTO() {
        // Arrange
        // Create Publisher
        Publisher publisher = new Publisher();
        publisher.setPublisherId(1L);
        publisher.setPublisherName("Complete Publisher");
        publisher.setImageUrl("http://example.com/complete-publisher.jpg");

        // Create Tags
        Tag tag1 = new Tag();
        tag1.setTagId(1);
        tag1.setTagName("RPG");

        Set<Tag> tags = new HashSet<>();
        tags.add(tag1);

        // Create Media
        Media media1 = new Media();
        media1.setMediaId(1L);
        media1.setUrl("http://example.com/complete-image.jpg");
        media1.setType("screenshot");

        List<Media> mediaList = new ArrayList<>();
        mediaList.add(media1);

        // Create Game
        Game game = new Game();
        game.setGameId(200L);
        game.setName("Complete Game");
        game.setPublisher(publisher);
        game.setReleaseDate(LocalDate.of(2023, 12, 1));
        game.setState(true); // Fixed: Boolean instead of String
        game.setPrice(new BigDecimal("49.99"));
        game.setShortDescription("Complete short description");
        game.setFullDescription("Complete full description");
        game.setTotalPurchased(5000);
        game.setTags(tags);
        game.setMedia(mediaList);
        game.setOs("Windows 10");
        game.setStorage("100 GB");
        game.setProcessor("Intel i7");
        game.setMemory("16 GB RAM");
        game.setAdditionalNotes("Complete additional notes");
        game.setGraphics("RTX 3070");

        // Create User
        User user = new User();
        user.setUserId(1L);

        // Create Library
        LibraryId libraryId = new LibraryId();
        libraryId.setUserId(1L);
        libraryId.setGameId(200L);

        Library library = new Library();
        library.setId(libraryId);
        library.setUser(user);
        library.setGame(game);
        library.setDateAdded(LocalDateTime.of(2023, 12, 15, 14, 30));
        library.setPlaytimeInMillis(7200000L); // 2 hours

        // Act
        LibraryGameDTO result = libraryService.mapLibraryEntryToDto(library);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getGameDetail());

        // Verify game details
        assertEquals(200L, result.getGameDetail().getGameId());
        assertEquals("Complete Game", result.getGameDetail().getName());
        assertEquals(new BigDecimal("49.99"), result.getGameDetail().getPrice());
        assertEquals("Complete short description", result.getGameDetail().getShortDescription());
        assertEquals("Complete full description", result.getGameDetail().getFullDescription());
        assertEquals(Integer.valueOf(5000), result.getGameDetail().getTotalPurchased());
        assertEquals(Boolean.TRUE, result.getGameDetail().getState()); // Fixed: Boolean comparison
        assertEquals(LocalDate.of(2023, 12, 1), result.getGameDetail().getReleaseDate());

        // Verify publisher
        assertNotNull(result.getGameDetail().getPublisher());
        assertEquals(Long.valueOf(1L), result.getGameDetail().getPublisher().getPublisherId());
        assertEquals("Complete Publisher", result.getGameDetail().getPublisher().getPublisherName());
        assertEquals("http://example.com/complete-publisher.jpg", result.getGameDetail().getPublisher().getImageUrl());

        // Verify tags
        assertNotNull(result.getGameDetail().getTags());
        assertEquals(1, result.getGameDetail().getTags().size());

        // Verify media
        assertNotNull(result.getGameDetail().getMedia());
        assertEquals(1, result.getGameDetail().getMedia().size());

        // Verify system requirements
        assertEquals("Windows 10", result.getGameDetail().getOs());
        assertEquals("100 GB", result.getGameDetail().getStorage());
        assertEquals("Intel i7", result.getGameDetail().getProcessor());
        assertEquals("16 GB RAM", result.getGameDetail().getMemory());
        assertEquals("Complete additional notes", result.getGameDetail().getAdditionalNotes());
        assertEquals("RTX 3070", result.getGameDetail().getGraphics());

        // Verify library specific fields
        assertEquals(LocalDateTime.of(2023, 12, 15, 14, 30), result.getDateAdded());
        assertEquals(Long.valueOf(7200000L), result.getPlaytimeInMillis());
    }

    @Test
    public void mapLibraryEntryToDto_gameWithoutPublisher_handlesNullPublisher() {
        // Arrange
        Game game = new Game();
        game.setGameId(300L);
        game.setName("Game Without Publisher");
        game.setPrice(new BigDecimal("9.99"));
        game.setPublisher(null); // No publisher

        User user = new User();
        user.setUserId(1L);

        LibraryId libraryId = new LibraryId();
        libraryId.setUserId(1L);
        libraryId.setGameId(300L);

        Library library = new Library();
        library.setId(libraryId);
        library.setUser(user);
        library.setGame(game);
        library.setDateAdded(LocalDateTime.now());

        // Act
        LibraryGameDTO result = libraryService.mapLibraryEntryToDto(library);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getGameDetail());
        assertEquals(300L, result.getGameDetail().getGameId());
        assertEquals("Game Without Publisher", result.getGameDetail().getName());
        assertNull(result.getGameDetail().getPublisher());
    }

    @Test
    public void mapLibraryEntryToDto_gameWithoutTagsAndMedia_handlesNullCollections() {
        // Arrange
        Game game = new Game();
        game.setGameId(400L);
        game.setName("Minimal Game");
        game.setPrice(new BigDecimal("5.99"));
        game.setTags(null); // No tags
        game.setMedia(null); // No media

        User user = new User();
        user.setUserId(1L);

        LibraryId libraryId = new LibraryId();
        libraryId.setUserId(1L);
        libraryId.setGameId(400L);

        Library library = new Library();
        library.setId(libraryId);
        library.setUser(user);
        library.setGame(game);
        library.setDateAdded(LocalDateTime.now());

        // Act
        LibraryGameDTO result = libraryService.mapLibraryEntryToDto(library);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getGameDetail());
        assertEquals(400L, result.getGameDetail().getGameId());
        assertEquals("Minimal Game", result.getGameDetail().getName());
        assertNull(result.getGameDetail().getTags());
        assertNull(result.getGameDetail().getMedia());
    }

    @Test
    public void mapLibraryEntryToDto_libraryWithNullDateAdded_handlesNullDate() {
        // Arrange
        Game game = new Game();
        game.setGameId(500L);
        game.setName("Game With Null Date");
        game.setPrice(new BigDecimal("15.99"));

        User user = new User();
        user.setUserId(1L);

        LibraryId libraryId = new LibraryId();
        libraryId.setUserId(1L);
        libraryId.setGameId(500L);

        Library library = new Library();
        library.setId(libraryId);
        library.setUser(user);
        library.setGame(game);
        library.setDateAdded(null); // No date added
        library.setPlaytimeInMillis(0L);

        // Act
        LibraryGameDTO result = libraryService.mapLibraryEntryToDto(library);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getGameDetail());
        assertEquals(500L, result.getGameDetail().getGameId());
        assertEquals("Game With Null Date", result.getGameDetail().getName());
        assertNull(result.getDateAdded());
        assertEquals(Long.valueOf(0L), result.getPlaytimeInMillis());
    }

    @Test
    public void mapLibraryEntryToDto_gameWithEmptyCollections_handlesEmptyCollections() {
        // Arrange
        Game game = new Game();
        game.setGameId(600L);
        game.setName("Game With Empty Collections");
        game.setPrice(new BigDecimal("25.99"));
        game.setTags(new HashSet<>()); // Empty tags
        game.setMedia(new ArrayList<>()); // Empty media

        User user = new User();
        user.setUserId(1L);

        LibraryId libraryId = new LibraryId();
        libraryId.setUserId(1L);
        libraryId.setGameId(600L);

        Library library = new Library();
        library.setId(libraryId);
        library.setUser(user);
        library.setGame(game);
        library.setDateAdded(LocalDateTime.now());

        // Act
        LibraryGameDTO result = libraryService.mapLibraryEntryToDto(library);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getGameDetail());
        assertEquals(600L, result.getGameDetail().getGameId());
        assertEquals("Game With Empty Collections", result.getGameDetail().getName());
        
        // Empty collections should be converted to empty collections in DTO
        assertNotNull(result.getGameDetail().getTags());
        assertTrue(result.getGameDetail().getTags().isEmpty());
        
        assertNotNull(result.getGameDetail().getMedia());
        assertTrue(result.getGameDetail().getMedia().isEmpty());
    }
}