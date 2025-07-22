package com.se1933g01.steamclonebackend.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.se1933g01.steamclonebackend.dto.AddingGameRequestDTO;
import com.se1933g01.steamclonebackend.dto.FeedbackDTO;
import com.se1933g01.steamclonebackend.entity.game.Game;
import com.se1933g01.steamclonebackend.entity.game.Tag;
import com.se1933g01.steamclonebackend.entity.request.AddingGameRequest;
import com.se1933g01.steamclonebackend.entity.request.Feedback;
import com.se1933g01.steamclonebackend.entity.request.PublisherApplyRequest;
import com.se1933g01.steamclonebackend.entity.request.Request;
import com.se1933g01.steamclonebackend.entity.user.Publisher;
import com.se1933g01.steamclonebackend.entity.user.Role;
import com.se1933g01.steamclonebackend.entity.user.User;
import com.se1933g01.steamclonebackend.repository.AddingGameRequestRepo;
import com.se1933g01.steamclonebackend.repository.FeedbackRepo;
import com.se1933g01.steamclonebackend.repository.GameRepo;
import com.se1933g01.steamclonebackend.repository.MediaRepo;
import com.se1933g01.steamclonebackend.repository.PublisherApplyRequestRepo;
import com.se1933g01.steamclonebackend.repository.PublisherRepo;
import com.se1933g01.steamclonebackend.repository.RequestRepo;
import com.se1933g01.steamclonebackend.repository.RoleRepo;
import com.se1933g01.steamclonebackend.repository.TagRepository;
import com.se1933g01.steamclonebackend.repository.UserRepo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {

    private RequestService requestService;

    // Real instance
    private ModelMapper modelMapper;

    // Mocks for dependencies
    @Mock private AddingGameRequestRepo addingGameRequestRepo;
    @Mock private PublisherApplyRequestRepo publisherApplyRequestRepo;
    @Mock private RequestRepo requestRepo;
    @Mock private UserRepo userRepo;
    @Mock private GameRepo gameRepo;
    @Mock private MediaRepo mediaRepo;
    @Mock private PublisherRepo publisherRepo;
    @Mock private FeedbackRepo feedbackRepo;
    @Mock private RoleRepo roleRepo;
    @Mock private TagRepository tagRepository;
    @Mock private CloudinaryService cloudinaryService;
    @Mock private R2StorageService r2StorageService;
    @Captor private ArgumentCaptor<Feedback> feedbackCaptor;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this); // Initializes mocks and captors

        modelMapper = new ModelMapper(); // Real ModelMapper
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT); // Important!


        // Construct the service with all dependencies
        requestService = new RequestService(
            addingGameRequestRepo,
            publisherApplyRequestRepo,
            requestRepo,
            modelMapper,
            userRepo,
            gameRepo,
            mediaRepo,
            publisherRepo,
            feedbackRepo,
            roleRepo,
            tagRepository,
            cloudinaryService,
            r2StorageService
        );
    }

    @Test
    public void testAddFeedback_SavesExpectedFeedback() {
        Long userId = 1L;

        // Given DTO input
        FeedbackDTO dto = new FeedbackDTO();
        dto.setSubject("feedback title");
        dto.setMessage("feedback content");
        dto.setMediaUrls(Arrays.asList("http://example.com", "https://example.com"));

        // Given stubs
        User mockUser = new User();
        Request mockRequest = new Request();

        when(userRepo.findById(userId)).thenReturn(Optional.of(mockUser));
        when(requestRepo.save(any(Request.class))).thenReturn(mockRequest);

        // When
        requestService.addFeedback(dto, userId);

        // Then
        verify(feedbackRepo).save(feedbackCaptor.capture());
        Feedback savedFeedback = feedbackCaptor.getValue();

        assertEquals("feedback title", savedFeedback.getSubject());
        assertEquals("feedback content", savedFeedback.getMessage());
    }

    @Test
    public void testAddFeedback_SubjectEmptyFields_ShouldThrow() {
        FeedbackDTO dto = new FeedbackDTO();
        dto.setSubject(""); // blank
        dto.setMessage("123");  
        Long userId = 1L;

        assertThrows(IllegalArgumentException.class, () -> {
            requestService.addFeedback(dto, userId);
        });

        verifyNoInteractions(feedbackRepo); // nothing should be saved
    }

    @Test
    public void testAddFeedback_MessageEmptyFields_ShouldThrow() {
        FeedbackDTO dto = new FeedbackDTO();
        dto.setSubject("abc");
        dto.setMessage("");  // empty
        Long userId = 1L;

        assertThrows(IllegalArgumentException.class, () -> {
            requestService.addFeedback(dto, userId);
        });

        verifyNoInteractions(feedbackRepo); // nothing should be saved
    }

    @Test
    public void testAddFeedback_UserNotFound_ShouldThrow() {
        Long userId = 404L;
        FeedbackDTO dto = new FeedbackDTO();
        dto.setSubject("feedback title");
        dto.setMessage("feedback content");
        dto.setMediaUrls(Arrays.asList("http://example.com", "https://example.com"));
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            requestService.addFeedback(dto, userId);
        });

        verify(requestRepo, never()).save(any());
        verify(feedbackRepo, never()).save(any());
    }

    @Test
    void addGame_shouldCreateRequestAndSaveGame_whenUserExists() {
        // Prepare mock data
        Long userId = 1L;
        AddingGameRequestDTO dto = new AddingGameRequestDTO();
        dto.setGameId(101L);
        dto.setGameName("Test Game");
        dto.setGameUrl("http://example.com/game");

        User user = new User();
        user.setUserId(userId);

        Request request = new Request();
        request.setRequestType("Game Submission");
        request.setUser(user);

        // Stub repo calls
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepo.save(any(Request.class))).thenAnswer(invocation -> {
            Request req = invocation.getArgument(0);
            req.setRequestId(999L); // Simulate DB save
            return req;
        });

        when(addingGameRequestRepo.save(any(AddingGameRequest.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Run
        requestService.addGame(dto, userId);

        // Verify
        verify(userRepo).findById(userId);
        verify(requestRepo).save(any(Request.class));
        verify(addingGameRequestRepo).save(any(AddingGameRequest.class));
    }

    @Test
    void addGame_shouldThrowException_whenUserNotFound() {
        Long userId = 999L;

        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            requestService.addGame(new AddingGameRequestDTO(), userId)
        );

        assertEquals("User not found", ex.getMessage());
        verify(userRepo).findById(userId);
        verifyNoMoreInteractions(requestRepo, addingGameRequestRepo);
    }
    @Test
    void approveGame_shouldCreateNewGame_whenGameIdIsNull() {
        Long requestId = 1L;

        // Mocks
        User user = new User(); user.setUserId(100L);
        Request request = new Request(); request.setUser(user);

        AddingGameRequest addingGameRequest = new AddingGameRequest();
        addingGameRequest.setGameId(null);
        addingGameRequest.setGameName("New Game");
        addingGameRequest.setPrice(new BigDecimal("59.99"));
        addingGameRequest.setMediaUrls(List.of("img1.png", "img2.png"));
        addingGameRequest.setTags(List.of(1, 2));

        Publisher publisher = new Publisher();
        List<Tag> tags = List.of(new Tag(), new Tag());

        // Stubbing
        when(requestRepo.findById(requestId)).thenReturn(Optional.of(request));
        when(addingGameRequestRepo.findById(requestId)).thenReturn(Optional.of(addingGameRequest));
        when(publisherRepo.findById(user.getUserId())).thenReturn(Optional.of(publisher));
        when(tagRepository.findAllById(any())).thenReturn(tags);

        // Execute
        requestService.approveGame(requestId);

        // Verify interactions
        verify(gameRepo).save(any(Game.class));
        verify(mediaRepo).saveAll(anyList());
        verify(requestRepo).save(request);
        verify(tagRepository).findAllById(anyList());
    }

    @Test
    void approveGame_shouldUpdateExistingGame_whenGameIdIsPresent() {
        Long requestId = 2L;

        // Mocks
        User user = new User(); user.setUserId(101L);
        Request request = new Request(); request.setUser(user);

        AddingGameRequest addingGameRequest = new AddingGameRequest();
        addingGameRequest.setGameId(500L);
        addingGameRequest.setGameName("Update Game");
        addingGameRequest.setMediaUrls(List.of("update1.png"));
        addingGameRequest.setTags(List.of(3));

        Game existingGame = new Game();
        existingGame.setTags(new HashSet<>());

        Publisher publisher = new Publisher();
        List<Tag> tags = List.of(new Tag());

        // Stubbing
        when(requestRepo.findById(requestId)).thenReturn(Optional.of(request));
        when(addingGameRequestRepo.findById(requestId)).thenReturn(Optional.of(addingGameRequest));
        when(publisherRepo.findById(user.getUserId())).thenReturn(Optional.of(publisher));
        when(gameRepo.findById(addingGameRequest.getGameId())).thenReturn(Optional.of(existingGame));
        when(tagRepository.findAllById(any())).thenReturn(tags);

        // Execute
        requestService.approveGame(requestId);

        // Verify updates
        verify(mediaRepo).deleteByGame(existingGame);
        verify(gameRepo).save(existingGame);
        verify(mediaRepo).saveAll(anyList());
        verify(requestRepo).save(request);
    }

    @Test
    void approveGame_shouldThrowException_whenRequestNotFound() {
        when(requestRepo.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            requestService.approveGame(999L)
        );

        assertEquals("Request not found", ex.getMessage());
    }

    @Test
    void approveGame_shouldThrowException_whenAddingGameRequestNotFound() {
        Long requestId = 10L;
        Request request = new Request(); request.setUser(new User());

        when(requestRepo.findById(requestId)).thenReturn(Optional.of(request));
        when(addingGameRequestRepo.findById(requestId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            requestService.approveGame(requestId)
        );

        assertEquals("AddingGameRequest not found", ex.getMessage());
    }

    @Test
    void approveGame_shouldThrowException_whenPublisherNotFound() {
        Long requestId = 20L;
        User user = new User(); user.setUserId(200L);
        Request request = new Request(); request.setUser(user);
        AddingGameRequest addingGameRequest = new AddingGameRequest();

        when(requestRepo.findById(requestId)).thenReturn(Optional.of(request));
        when(addingGameRequestRepo.findById(requestId)).thenReturn(Optional.of(addingGameRequest));
        when(publisherRepo.findById(user.getUserId())).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            requestService.approveGame(requestId)
        );

        assertEquals("Publisher not found", ex.getMessage());
    }
    // @Test
    // void approvePublisher_shouldCreatePublisherAndUpdateUser_whenDataIsValid() {
    //     Long requestId = 1L;

    //     // Mocked entities
    //     User user = new User(); user.setUserId(100L);
    //     Request request = new Request(); request.setUser(user);
    //     PublisherApplyRequest applyRequest = new PublisherApplyRequest();
    //     applyRequest.setLegalName("Legal Inc.");
    //     applyRequest.setAddress("123 Steam St");
    //     applyRequest.setSocialNumber("SN123");
    //     applyRequest.setCountry("VN");
    //     applyRequest.setCardNumber("CARD456");
    //     applyRequest.setPublisherName("Steam Partner");
    //     applyRequest.setImageUrl("http://image.url/logo.png");

    //     Role role = new Role(); role.setRoleId(2L); role.setRoleName("Publisher");


    //     // Stubbing
    //     when(requestRepo.findById(requestId)).thenReturn(Optional.of(request));
    //     when(publisherApplyRequestRepo.findById(requestId)).thenReturn(Optional.of(applyRequest));
    //     when(userRepo.findById(user.getUserId())).thenReturn(Optional.of(user));
    //     when(roleRepo.findById(2L)).thenReturn(Optional.of(role));

    //     // Execute
    //     requestService.approvePublisher(requestId);

    //     // Verify persistence and updates
    //     verify(entityManager).persist(any(Publisher.class));
    //     verify(entityManager).flush();
    //     verify(userRepo).save(user);
    //     verify(requestRepo).save(request);

    //     // Validate user role assignment
    //     assertEquals(role, user.getRole());
    //     assertEquals((Integer)1,request.getRequestState());
    // }

    // @Test
    // void approvePublisher_shouldThrowException_whenRequestNotFound() {
    //     when(requestRepo.findById(999L)).thenReturn(Optional.empty());

    //     RuntimeException ex = assertThrows(RuntimeException.class, () ->
    //         requestService.approvePublisher(999L)
    //     );
    //     assertEquals("REQUEST_NOT_FOUND_MESSAGE", ex.getMessage());
    // }

    // @Test
    // void approvePublisher_shouldThrowException_whenApplyRequestNotFound() {
    //     Long requestId = 2L;
    //     Request request = new Request(); request.setUser(new User());

    //     when(requestRepo.findById(requestId)).thenReturn(Optional.of(request));
    //     when(publisherApplyRequestRepo.findById(requestId)).thenReturn(Optional.empty());

    //     RuntimeException ex = assertThrows(RuntimeException.class, () ->
    //         requestService.approvePublisher(requestId)
    //     );
    //     assertEquals("PublisherApplyRequest not found", ex.getMessage());
    // }

    // @Test
    // void approvePublisher_shouldThrowException_whenUserNotFound() {
    //     Long requestId = 3L;
    //     User user = new User(); user.setUserId(100L);
    //     Request request = new Request(); request.setUser(user);

    //     when(requestRepo.findById(requestId)).thenReturn(Optional.of(request));
    //     when(publisherApplyRequestRepo.findById(requestId)).thenReturn(Optional.of(new PublisherApplyRequest()));
    //     when(userRepo.findById(user.getUserId())).thenReturn(Optional.empty());

    //     RuntimeException ex = assertThrows(RuntimeException.class, () ->
    //         requestService.approvePublisher(requestId)
    //     );
    //     assertEquals("USER_NOT_FOUND_MESSAGE", ex.getMessage());
    // }

    // @Test
    // void approvePublisher_shouldThrowException_whenRoleNotFound() {
    //     Long requestId = 4L;
    //     User user = new User(); user.setUserId(123L);
    //     Request request = new Request(); request.setUser(user);

    //     when(requestRepo.findById(requestId)).thenReturn(Optional.of(request));
    //     when(publisherApplyRequestRepo.findById(requestId)).thenReturn(Optional.of(new PublisherApplyRequest()));
    //     when(userRepo.findById(user.getUserId())).thenReturn(Optional.of(user));
    //     when(roleRepo.findById(2L)).thenReturn(Optional.empty());

    //     RuntimeException ex = assertThrows(RuntimeException.class, () ->
    //         requestService.approvePublisher(requestId)
    //     );
    //     assertEquals("Role not found", ex.getMessage());
    // }

    @Test
    void approveFeedback_shouldUpdateFeedbackAndRequest_whenValidRequestId() {
        // Setup
        Long requestId = 1L;
        String response = "Thank you for your feedback!";

        Request request = new Request();
        Feedback feedback = new Feedback();

        // Stubbing repository behavior
        when(requestRepo.findById(requestId)).thenReturn(Optional.of(request));
        when(feedbackRepo.findById(requestId)).thenReturn(Optional.of(feedback));

        // Execute
        requestService.approveFeedback(requestId, response);

        // Verify
        verify(requestRepo).findById(requestId);
        verify(feedbackRepo).findById(requestId);

        verify(feedbackRepo).save(feedback);
        verify(requestRepo).save(request);

        // Assert state changes
        assertEquals(response, feedback.getResponse());
        assertEquals((Integer)1, request.getRequestState());
    }

    @Test
    void approveFeedback_shouldThrowException_whenRequestNotFound() {
        when(requestRepo.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            requestService.approveFeedback(999L, "response")
        );

        assertEquals("Request not found", ex.getMessage());
        verify(requestRepo).findById(999L);
        verifyNoMoreInteractions(feedbackRepo);
    }

    @Test
    void approveFeedback_shouldThrowException_whenFeedbackNotFound() {
        Long requestId = 2L;
        Request request = new Request();
        when(requestRepo.findById(requestId)).thenReturn(Optional.of(request));
        when(feedbackRepo.findById(requestId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            requestService.approveFeedback(requestId, "response")
        );

        assertEquals("Feedback not found", ex.getMessage());
        verify(requestRepo).findById(requestId);
        verify(feedbackRepo).findById(requestId);
    }
    @Test
    void rejectFeedback_shouldUpdateRequestState() {
        Long requestId = 1L;
        Request request = new Request();

        when(requestRepo.findById(requestId)).thenReturn(Optional.of(request));

        requestService.rejectFeedback(requestId);

        verify(requestRepo).findById(requestId);
        assertEquals((Integer)2, request.getRequestState()); // Rejected
        verify(requestRepo).save(request);
    }

    @Test
    void rejectPublisher_shouldUpdateRequestState() {
        Long requestId = 2L;
        Request request = new Request();

        when(requestRepo.findById(requestId)).thenReturn(Optional.of(request));

        requestService.rejectPublisher(requestId);

        verify(requestRepo).findById(requestId);
        assertEquals((Integer)2, request.getRequestState());
        verify(requestRepo).save(request);
    }

    @Test
    void rejectGame_shouldUpdateDeclineMessageAndRequestState() {
        Long requestId = 3L;
        String declineMessage = "Not eligible";

        Request request = new Request();
        AddingGameRequest addingGameRequest = new AddingGameRequest();

        when(addingGameRequestRepo.findById(requestId)).thenReturn(Optional.of(addingGameRequest));
        when(requestRepo.findById(requestId)).thenReturn(Optional.of(request));

        requestService.rejectGame(requestId, declineMessage);

        verify(addingGameRequestRepo).findById(requestId);
        verify(addingGameRequestRepo).save(addingGameRequest);
        assertEquals(declineMessage, addingGameRequest.getDeclineMessage());

        verify(requestRepo).findById(requestId);
        assertEquals((Integer)2, request.getRequestState());
        verify(requestRepo).save(request);
    }

    @Test
    void rejectGame_shouldThrowException_whenAddingGameRequestNotFound() {
        Long requestId = 4L;
        when(addingGameRequestRepo.findById(requestId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            requestService.rejectGame(requestId, "Declined")
        );

        assertEquals("AddingGameRequest not found", ex.getMessage());
    }

    @Test
    void rejectRequestById_shouldThrowException_whenRequestNotFound() {
        Long requestId = 5L;
        when(requestRepo.findById(requestId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            requestService.rejectFeedback(requestId)
        );

        assertEquals("Request not found", ex.getMessage());
    }







}
