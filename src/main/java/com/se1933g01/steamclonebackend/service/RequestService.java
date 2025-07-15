// Author: Vu Quoc Hoang - HE194781
package com.se1933g01.steamclonebackend.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.se1933g01.steamclonebackend.dto.AddingGameRequestDTO;
import com.se1933g01.steamclonebackend.dto.FeedbackDTO;
import com.se1933g01.steamclonebackend.dto.PublisherApplyRequestDTO;
import com.se1933g01.steamclonebackend.entity.game.Game;
import com.se1933g01.steamclonebackend.entity.game.Media;
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
import jakarta.transaction.Transactional;

@Service
public class RequestService {

    private final RoleRepo roleRepo;
    private static final String USER_NOT_FOUND_MESSAGE = "User not found";
    private static final String REQUEST_NOT_FOUND_MESSAGE = "Request not found";
    private final AddingGameRequestRepo addingGameRequestRepo;
    private final PublisherApplyRequestRepo publisherApplyRequestRepo;
    private final RequestRepo requestRepo;
    private final ModelMapper modelMapper;
    private final UserRepo userRepo;
    private final GameRepo gameRepo;
    private final MediaRepo mediaRepo;
    private final PublisherRepo publisherRepo;
    private final FeedbackRepo feedbackRepo;
    private final TagRepository tagRepository;
    public RequestService(
        AddingGameRequestRepo addingGameRequestRepo,
        PublisherApplyRequestRepo publisherApplyRequestRepo,
        RequestRepo requestRepo,
        ModelMapper modelMapper,
        UserRepo userRepo,
        GameRepo gameRepo,
        MediaRepo mediaRepo,
        PublisherRepo publisherRepo,
        FeedbackRepo feedbackRepo,
        RoleRepo roleRepo,
        TagRepository tagRepository
    ) {
        this.addingGameRequestRepo = addingGameRequestRepo;
        this.publisherApplyRequestRepo = publisherApplyRequestRepo;
        this.requestRepo = requestRepo;
        this.modelMapper = modelMapper;
        this.userRepo = userRepo;
        this.gameRepo = gameRepo;
        this.mediaRepo = mediaRepo;
        this.publisherRepo = publisherRepo;
        this.feedbackRepo = feedbackRepo;
        this.roleRepo = roleRepo;
        this.tagRepository = tagRepository;
    }
    @PersistenceContext
    private EntityManager entityManager;
    @Transactional
    public void addGame(AddingGameRequestDTO addingGameRequestDTO, Long userId) {
        System.out.println(addingGameRequestDTO);
        User user = userRepo.findById(userId).orElseThrow(()-> new RuntimeException(USER_NOT_FOUND_MESSAGE));
        Request request = new Request();
        request.setUser(user);
        request.setRequestType("Game Submission");
        request.setTimeCreated(LocalDate.now());
        request.setRequestState(0);
        Request savedRequest = requestRepo.save(request); 
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        AddingGameRequest addingGameRequest = modelMapper.map(addingGameRequestDTO, AddingGameRequest.class);
        addingGameRequest.setReleaseDate(LocalDate.now());
        addingGameRequest.setRequest(savedRequest);
        addingGameRequestRepo.save(addingGameRequest);
    }

    public void approveGame(Long requestID) {
        Request request = requestRepo.findById(requestID).orElseThrow(()-> new RuntimeException(REQUEST_NOT_FOUND_MESSAGE));
        AddingGameRequest addingGameRequest = addingGameRequestRepo.findById(requestID).orElseThrow(()-> new RuntimeException("AddingGameRequest not found"));
        Publisher publisher = publisherRepo.findById(request.getUser().getUserId()).orElseThrow(()-> new RuntimeException("Publisher not found"));
        Game game = new Game();
        game.setPublisher(publisher);
        game.setName(addingGameRequest.getGameName());
        game.setReleaseDate(LocalDate.now());
        game.setState(true);
        game.setPrice(addingGameRequest.getPrice());
        game.setShortDescription(addingGameRequest.getShortDescription());
        game.setFullDescription(addingGameRequest.getFullDescription());
        game.setTotalPurchased(0);
        game.setOs(addingGameRequest.getOs());
        game.setStorage(addingGameRequest.getStorage());
        game.setProcessor(addingGameRequest.getProcessor());
        game.setMemory(addingGameRequest.getMemory());
        game.setGraphics(addingGameRequest.getGraphics());
        game.setAdditionalNotes(addingGameRequest.getAdditionalNotes());
        gameRepo.save(game);

        List<String> mediaUrls = addingGameRequest.getMediaUrls();
        List<Media> mediaList = mediaUrls.stream().map(url -> Media.builder().game(game).url(url).type("Image").build()).collect(Collectors.toList());
        mediaRepo.saveAll(mediaList);

        List<Integer> tagIds = addingGameRequest.getTags();
        List<Tag> tags = tagRepository.findAllById(tagIds);
        Set<Tag> tagSet = new HashSet<>(tags); 
        game.setTags(tagSet);
        gameRepo.save(game);

        request.setRequestState(1);
        requestRepo.save(request);
    }
    @Transactional
    public void approvePublisher(Long requestID) {
        Request request = requestRepo.findById(requestID).orElseThrow(()-> new RuntimeException(REQUEST_NOT_FOUND_MESSAGE));
        PublisherApplyRequest publisherApplyRequest = publisherApplyRequestRepo.findById(requestID).orElseThrow(()-> new RuntimeException("PublisherApplyRequest not found"));
        User user = userRepo.findById(request.getUser().getUserId()).orElseThrow(()-> new RuntimeException(USER_NOT_FOUND_MESSAGE));
        Publisher publisher = new Publisher();
        Role role = roleRepo.findById(2L).orElseThrow(()-> new RuntimeException("Role not found"));
        publisher.setPublisherId(user.getUserId());
        publisher.setUser(user);
        publisher.setLegalName(publisherApplyRequest.getLegalName());
        publisher.setAddress(publisherApplyRequest.getAddress());
        publisher.setSocialNumber(publisherApplyRequest.getSocialNumber());
        publisher.setCountry(publisherApplyRequest.getCountry());
        publisher.setCardNumber(publisherApplyRequest.getCardNumber());
        publisher.setPublisherName(publisherApplyRequest.getPublisherName());
        publisher.setImageUrl(publisherApplyRequest.getImageUrl());
        request.setRequestState(1);
        entityManager.persist(publisher);
        entityManager.flush();
        requestRepo.save(request);
        user.setRole(role);
        userRepo.save(user);
    }
    public void approveFeedback(Long requestId,String response){
        Request request = requestRepo.findById(requestId).orElseThrow(()-> new RuntimeException(REQUEST_NOT_FOUND_MESSAGE));
        Feedback feedback = feedbackRepo.findById(requestId).orElseThrow(()-> new RuntimeException("Feedback not found"));
        feedback.setResponse(response);
        feedbackRepo.save(feedback);
        request.setRequestState(1);
        requestRepo.save(request);
    }
    public void rejectFeedback(Long requestId){
        rejectRequestById(requestId);
    }
    public void rejectPublisher(Long requestID) {
        rejectRequestById(requestID);
    }
    public void rejectGame(Long requestID) {
        rejectRequestById(requestID);
    }
    private void rejectRequestById(Long requestID) {
        Request request = requestRepo.findById(requestID)
            .orElseThrow(() -> new RuntimeException(REQUEST_NOT_FOUND_MESSAGE));
        request.setRequestState(2); // 2 = Rejected
        requestRepo.save(request);
    }

    public Page<AddingGameRequestDTO> getAllAddingGameRequest(Pageable pageable) {
        Page<AddingGameRequest> addingGameRequestPage = addingGameRequestRepo.findAllByRequestStateZero(pageable);
        
        return addingGameRequestPage.map(addingGameRequest -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            AddingGameRequestDTO addingGameRequestDTO = modelMapper.map(addingGameRequest, AddingGameRequestDTO.class);
            addingGameRequestDTO.setPublisherName(addingGameRequest.getRequest().getUser().getPublisher().getPublisherName());
            addingGameRequestDTO.setSendDate(addingGameRequest.getRequest().getTimeCreated().toString());
            addingGameRequestDTO.setPublisherId(addingGameRequest.getRequest().getUser().getPublisher().getPublisherId().toString());
            return addingGameRequestDTO;
        });
    }
    public Page<PublisherApplyRequestDTO> getAllPublisherApplyRequest(Pageable pageable) {
        Page<PublisherApplyRequest> publisherApplyRequestPage = publisherApplyRequestRepo.findAllByRequestStateZero(pageable);
        return publisherApplyRequestPage.map(publisherApplyRequest -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PublisherApplyRequestDTO publisherApplyRequestDTO = modelMapper.map(publisherApplyRequest, PublisherApplyRequestDTO.class);
            publisherApplyRequestDTO.setUsername(publisherApplyRequest.getRequest().getUser().getUsername());
            publisherApplyRequestDTO.setCreatedDate(publisherApplyRequest.getRequest().getTimeCreated().toString());
            publisherApplyRequestDTO.setUserId(publisherApplyRequest.getRequest().getUser().getUserId().toString());
            return publisherApplyRequestDTO;
        });
    }
    public Page<FeedbackDTO> getAllFeedback(Pageable pageable) {
        Page<Feedback> feedbackPage = feedbackRepo.findAllByRequestStateZero(pageable);
        return feedbackPage.map(feedback -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            FeedbackDTO feedbackDTO = modelMapper.map(feedback, FeedbackDTO.class);
            feedbackDTO.setUserName(feedbackRepo.findUserNameByRequestId(feedback.getRequest().getRequestId()));
            feedbackDTO.setUserId(Long.parseLong(feedbackRepo.findUserIdByRequestId(feedback.getRequest().getRequestId())));
            feedbackDTO.setCreatedDate(feedback.getRequest().getTimeCreated().toString());
            return feedbackDTO;
        });
    }

    public Page<FeedbackDTO> getAllFeedbackFromUser(Long userId, Pageable pageable) {
        Page<Feedback> feedbackPage = feedbackRepo.findAllByUserId(userId, pageable);
        return feedbackPage.map(feedback -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            FeedbackDTO feedbackDTO = modelMapper.map(feedback, FeedbackDTO.class);
            feedbackDTO.setUserName(feedbackRepo.findUserNameByRequestId(feedback.getRequest().getRequestId()));
            feedbackDTO.setUserId(Long.parseLong(feedbackRepo.findUserIdByRequestId(feedback.getRequest().getRequestId())));
            feedbackDTO.setStatus(feedback.getRequest().getRequestState());
            feedbackDTO.setCreatedDate(feedback.getRequest().getTimeCreated().toString());
            return feedbackDTO;
        });
    }

    public AddingGameRequestDTO getGameDetails(Long requestId){
        AddingGameRequest addingGameRequest = addingGameRequestRepo.findById(requestId).orElseThrow(()-> new RuntimeException("AddingGameRequest not found"));
        int status = addingGameRequest.getRequest().getRequestState();
        if (status == 1 || status == 2) {
            return null; 
        }
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        AddingGameRequestDTO addingGameRequestDTO = modelMapper.map(addingGameRequest, AddingGameRequestDTO.class);
        addingGameRequestDTO.setPublisherName(addingGameRequestRepo.findPublisherNameByRequestId(requestId));
        addingGameRequestDTO.setPublisherId(addingGameRequestRepo.findPublisherIdByRequestId(requestId));
        return addingGameRequestDTO;
    }
    public PublisherApplyRequestDTO getPublisherDetails(Long requestId){
        PublisherApplyRequest publisherApplyRequest = publisherApplyRequestRepo.findById(requestId).orElseThrow(()-> new RuntimeException("PublisherApplyRequest not found"));
        int status = publisherApplyRequest.getRequest().getRequestState();
        if (status == 1 || status == 2) {
            return null; 
        }
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        PublisherApplyRequestDTO publisherApplyRequestDTO = modelMapper.map(publisherApplyRequest, PublisherApplyRequestDTO.class);
        publisherApplyRequestDTO.setUserId(publisherApplyRequestRepo.findUserIdByRequestId(requestId));
        return publisherApplyRequestDTO;
    }

    public FeedbackDTO getFeedbackDetails(Long requestId){
        Feedback feedback = feedbackRepo.findById(requestId).orElseThrow(()-> new RuntimeException("Feedback not found"));
        int status = feedback.getRequest().getRequestState();
        if (status == 1 || status == 2) {
            return null; 
        }
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        FeedbackDTO feedbackDTO = modelMapper.map(feedback, FeedbackDTO.class);
        feedbackDTO.setUserName(feedbackRepo.findUserNameByRequestId(requestId));
        feedbackDTO.setUserId(Long.parseLong(feedbackRepo.findUserIdByRequestId(requestId)));
        return feedbackDTO;
    }

    public FeedbackDTO getUserFeedbackDetails(Long requestId, Long userId) {
        Feedback feedback = feedbackRepo.findById(requestId).orElseThrow(()-> new RuntimeException("Feedback not found"));
        if(!feedback.getRequest().getUser().getUserId().equals(userId)){
            return null;
        }
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        FeedbackDTO feedbackDTO = modelMapper.map(feedback, FeedbackDTO.class);
        return feedbackDTO;
    }

    public PublisherApplyRequestDTO getUserPublisherApplyDetails(Long userId) {
        PublisherApplyRequest publisherApplyRequest = publisherApplyRequestRepo.findByUserIdAndRequestStateZeroAndTwo(userId);
        if(!publisherApplyRequest.getRequest().getUser().getUserId().equals(userId)){
            return null;
        }
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(publisherApplyRequest, PublisherApplyRequestDTO.class);
    }


    public void addPublisher(PublisherApplyRequestDTO dto, Long userId) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        PublisherApplyRequest existingPAR = publisherApplyRequestRepo.findByRequestUserId(userId);
        Request request;

        if (existingPAR != null && existingPAR.getRequest() != null) {
            request = existingPAR.getRequest();
            request.setRequestState(0); // Reset or change as needed
            request.setTimeCreated(LocalDate.now());
            requestRepo.save(request);

            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PublisherApplyRequest updatedPAR = modelMapper.map(dto, PublisherApplyRequest.class);
            updatedPAR.setRequest(request); // Important: link updated Request
            publisherApplyRequestRepo.save(updatedPAR);
        } else {
            request = new Request();
            request.setUser(user);
            request.setRequestType("Publisher Submission");
            request.setTimeCreated(LocalDate.now());
            request.setRequestState(0);
            Request savedRequest = requestRepo.save(request);

            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            PublisherApplyRequest newPAR = modelMapper.map(dto, PublisherApplyRequest.class);
            newPAR.setRequest(savedRequest);
            publisherApplyRequestRepo.save(newPAR);
        }
    }


    public void addFeedback(FeedbackDTO feedbackDTO, Long userId){
        if (feedbackDTO.getSubject().trim() == null || feedbackDTO.getSubject().trim().isBlank() ||
        feedbackDTO.getMessage().trim() == null || feedbackDTO.getMessage().trim().isBlank()) {
        throw new IllegalArgumentException("Subject and message cannot be empty");
        }
        User user = userRepo.findById(userId).orElseThrow(()-> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE));
        Request request = new Request();
        request.setUser(user);
        request.setRequestType("Feedback");
        request.setTimeCreated(LocalDate.now());
        request.setRequestState(0);
        Request savedRequest = requestRepo.save(request); 
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Feedback feedback = modelMapper.map(feedbackDTO, Feedback.class);
        feedback.setRequest(savedRequest);
        feedbackRepo.save(feedback);
    }
    public boolean checkForGameExist(String gameName) {
        return gameRepo.existsByName(gameName) || addingGameRequestRepo.existsByGameName(gameName);
    }

    public void deleteFeedback(Long feedbackId, Long userId) {
        Feedback feedback = feedbackRepo.findById(feedbackId).orElseThrow(()-> new RuntimeException("Feedback not found"));
        if(!feedback.getRequest().getUser().getUserId().equals(userId)){
            return;
        }
        feedbackRepo.delete(feedback);
        requestRepo.delete(feedback.getRequest());
    }

}