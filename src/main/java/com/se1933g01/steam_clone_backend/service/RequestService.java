// // Author: Vu Quoc Hoang - HE194781
// package com.se1933g01.steam_clone_backend.service;

// import java.sql.Date;
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Map;
// import java.util.stream.Collector;
// import java.util.stream.Collectors;

// import org.modelmapper.ModelMapper;
// import org.modelmapper.TypeToken;
// import org.modelmapper.convention.MatchingStrategies;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;
// import org.springframework.web.multipart.support.MultipartFilter;

// import com.se1933g01.steam_clone_backend.dto.AddingGameRequestDTO;
// import com.se1933g01.steam_clone_backend.entity.game.Game;
// import com.se1933g01.steam_clone_backend.entity.game.Media;
// import com.se1933g01.steam_clone_backend.entity.request.AddingGameRequest;
// import com.se1933g01.steam_clone_backend.entity.request.Request;
// import com.se1933g01.steam_clone_backend.entity.user.Publisher;
// import com.se1933g01.steam_clone_backend.entity.user.User;
// import com.se1933g01.steam_clone_backend.repository.AddingGameRequestRepo;
// import com.se1933g01.steam_clone_backend.repository.GameRepository;
// import com.se1933g01.steam_clone_backend.repository.MediaRepo;
// import com.se1933g01.steam_clone_backend.repository.PublisherRepo;
// import com.se1933g01.steam_clone_backend.repository.RequestRepo;
// import com.se1933g01.steam_clone_backend.repository.UserRepo;

// import jakarta.transaction.Transactional;

// @Service
// public class RequestService {
//     @Autowired
//     private AddingGameRequestRepo addingGameRequestRepo;
//     @Autowired
//     private RequestRepo requestRepo;
//     @Autowired
//     private ModelMapper modelMapper;
//     @Autowired
//     private UserRepo userRepo;
//     @Autowired
//     private GameRepository gameRepo;
//     @Autowired
//     private MediaRepo mediaRepo;
//     @Autowired 
//     private PublisherRepo publisherRepo;
//     @Transactional
//     public void addGame(AddingGameRequestDTO addingGameRequestDTO, Long UserID) {
//         User user = userRepo.findById(UserID).orElseThrow(()-> new RuntimeException("User not found"));
//         Request request = new Request();
//         request.setUser(user);
//         request.setRequestType("Game Submission");
//         request.setTimeCreated(LocalDate.now());
//         request.setRequestState(0);
//         Request savedRequest = requestRepo.save(request); 
//         modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//         AddingGameRequest addingGameRequest = modelMapper.map(addingGameRequestDTO, AddingGameRequest.class);
//         System.out.println(addingGameRequest.getMediaUrls());
//         addingGameRequest.setReleaseDate(LocalDate.now());
//         addingGameRequest.setRequest(savedRequest);
//         addingGameRequestRepo.save(addingGameRequest);
//     }

//     public void approveGame(Long requestID, Long userID) {
//         Request request = requestRepo.findById(requestID).orElseThrow(()-> new RuntimeException("Request not found"));
//         AddingGameRequest addingGameRequest = addingGameRequestRepo.findById(requestID).orElseThrow(()-> new RuntimeException("AddingGameRequest not found"));
//         Publisher publisher = publisherRepo.findById(request.getUser().getUserID()).orElseThrow(()-> new RuntimeException("Publisher not found"));
//         Game game = new Game();
//         game.setPublisher(publisher);
//         game.setName(addingGameRequest.getGameName());
//         game.setReleaseDate(LocalDate.now());
//         game.setState(true);
//         game.setPrice(addingGameRequest.getPrice());
//         game.setShortDescription(addingGameRequest.getShortDescription());
//         game.setFullDescription(addingGameRequest.getFullDescription());
//         game.setTotalPurchased(0);
//         game.setOs(addingGameRequest.getOs());
//         game.setStorage(addingGameRequest.getStorage());
//         game.setProcessor(addingGameRequest.getProcessor());
//         game.setMemory(addingGameRequest.getMemory());
//         game.setGraphics(addingGameRequest.getGraphics());
//         game.setAdditionalNotes(addingGameRequest.getAdditionalNotes());
//         gameRepo.save(game);

//         List<String> mediaUrls = addingGameRequest.getMediaUrls();
//         List<Media> mediaList = mediaUrls.stream().map(url -> Media.builder().game(game).url(url).type("Image").build()).collect(Collectors.toList());
//         mediaRepo.saveAll(mediaList);

//     }
// }
