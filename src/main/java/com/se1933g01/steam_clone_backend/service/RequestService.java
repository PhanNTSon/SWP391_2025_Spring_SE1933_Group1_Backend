// Author: Vu Quoc Hoang - HE194781
package com.se1933g01.steam_clone_backend.service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MultipartFilter;

import com.se1933g01.steam_clone_backend.dto.AddingGameRequestDTO;
import com.se1933g01.steam_clone_backend.dto.FeedbackDTO;
import com.se1933g01.steam_clone_backend.dto.PublisherApplyRequestDTO;
import com.se1933g01.steam_clone_backend.entity.game.Game;
import com.se1933g01.steam_clone_backend.entity.game.Media;
import com.se1933g01.steam_clone_backend.entity.request.AddingGameRequest;
import com.se1933g01.steam_clone_backend.entity.request.Feedback;
import com.se1933g01.steam_clone_backend.entity.request.PublisherApplyRequest;
import com.se1933g01.steam_clone_backend.entity.request.Request;
import com.se1933g01.steam_clone_backend.entity.user.User;
import com.se1933g01.steam_clone_backend.entity.user.Publisher;
import com.se1933g01.steam_clone_backend.repository.AddingGameRequestRepo;
import com.se1933g01.steam_clone_backend.repository.FeedbackRepo;
import com.se1933g01.steam_clone_backend.repository.GameRepo;
import com.se1933g01.steam_clone_backend.repository.MediaRepo;
import com.se1933g01.steam_clone_backend.repository.PublisherApplyRequestRepo;
import com.se1933g01.steam_clone_backend.repository.PublisherRepo;
import com.se1933g01.steam_clone_backend.repository.RequestRepo;
import com.se1933g01.steam_clone_backend.repository.UserRepo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class RequestService {
    @Autowired
    private AddingGameRequestRepo addingGameRequestRepo;
    @Autowired
    private PublisherApplyRequestRepo publisherApplyRequestRepo;
    @Autowired
    private RequestRepo requestRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private GameRepo gameRepo;
    @Autowired
    private MediaRepo mediaRepo;
    @Autowired 
    private PublisherRepo publisherRepo;
    @Autowired
    private FeedbackRepo feedbackRepo;
    @PersistenceContext
    private EntityManager entityManager;
    @Transactional
    public void addGame(AddingGameRequestDTO addingGameRequestDTO, Long UserID) {
        User user = userRepo.findById(UserID).orElseThrow(()-> new RuntimeException("User not found"));
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
        Request request = requestRepo.findById(requestID).orElseThrow(()-> new RuntimeException("Request not found"));
        AddingGameRequest addingGameRequest = addingGameRequestRepo.findById(requestID).orElseThrow(()-> new RuntimeException("AddingGameRequest not found"));
        Publisher publisher = publisherRepo.findById(request.getUser().getUserID()).orElseThrow(()-> new RuntimeException("Publisher not found"));
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

        request.setRequestState(1);
        requestRepo.save(request);
    }
    @Transactional
    public void approvePublisher(Long requestID) {
        Request request = requestRepo.findById(requestID).orElseThrow(()-> new RuntimeException("Request not found"));
        PublisherApplyRequest publisherApplyRequest = publisherApplyRequestRepo.findById(requestID).orElseThrow(()-> new RuntimeException("PublisherApplyRequest not found"));
        User user = userRepo.findById(request.getUser().getUserID()).orElseThrow(()-> new RuntimeException("User not found"));
        Publisher publisher = new Publisher();
        publisher.setPublisherId(user.getUserID());
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

    }
    public void rejectPublisher(Long requestID) {
        Request request = requestRepo.findById(requestID).orElseThrow(()-> new RuntimeException("Request not found"));
        request.setRequestState(2);
        requestRepo.save(request);
    }
    public void rejectGame(Long requestID) {
        Request request = requestRepo.findById(requestID).orElseThrow(()-> new RuntimeException("Request not found"));
        request.setRequestState(2);
        requestRepo.save(request);
    }
    // public List<AddingGameRequestDTO> getAllAddingGameRequest(){
    //     List<AddingGameRequest> addingGameRequestList = addingGameRequestRepo.findAll();
    //     List<AddingGameRequestDTO> addingGameRequestDTOList = new ArrayList<>();
    //     for(AddingGameRequest addingGameRequest : addingGameRequestList){
    //         modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    //         AddingGameRequestDTO addingGameRequestDTO = modelMapper.map(addingGameRequest, AddingGameRequestDTO.class);
    //         addingGameRequestDTOList.add(addingGameRequestDTO);
    //     }
    //     return addingGameRequestDTOList;
    // }

    public Page<AddingGameRequestDTO> getAllAddingGameRequest(Pageable pageable) {
        Page<AddingGameRequest> addingGameRequestPage = addingGameRequestRepo.findAllByRequestStateZero(pageable);
        
        return addingGameRequestPage.map(addingGameRequest -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            return modelMapper.map(addingGameRequest, AddingGameRequestDTO.class);
        });
    }
    public Page<PublisherApplyRequestDTO> getAllPublisherApplyRequest(Pageable pageable) {
        Page<PublisherApplyRequest> publisherApplyRequestPage = publisherApplyRequestRepo.findAll(pageable);
        return publisherApplyRequestPage.map(publisherApplyRequest -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            return modelMapper.map(publisherApplyRequest, PublisherApplyRequestDTO.class);
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
        return publisherApplyRequestDTO;
    }

    public void addPublisher(PublisherApplyRequestDTO publisherApplyRequestDTO, Long UserID){
        User user = userRepo.findById(UserID).orElseThrow(()-> new RuntimeException("User not found"));
        Request request = new Request();
        request.setUser(user);
        request.setRequestType("Publisher Submission");
        request.setTimeCreated(LocalDate.now());
        request.setRequestState(0);
        Request savedRequest = requestRepo.save(request); 
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        PublisherApplyRequest publisherApplyRequest = modelMapper.map(publisherApplyRequestDTO, PublisherApplyRequest.class);
        publisherApplyRequest.setRequest(savedRequest);
        publisherApplyRequestRepo.save(publisherApplyRequest);
    }
    public void addFeedback(FeedbackDTO feedbackDTO, Long UserID){
        User user = userRepo.findById(UserID).orElseThrow(()-> new RuntimeException("User not found"));
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
}