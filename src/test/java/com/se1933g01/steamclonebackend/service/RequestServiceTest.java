package com.se1933g01.steamclonebackend.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
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

import com.se1933g01.steamclonebackend.dto.FeedbackDTO;
import com.se1933g01.steamclonebackend.entity.request.Feedback;
import com.se1933g01.steamclonebackend.entity.request.Request;
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

import jakarta.persistence.EntityNotFoundException;
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




}
