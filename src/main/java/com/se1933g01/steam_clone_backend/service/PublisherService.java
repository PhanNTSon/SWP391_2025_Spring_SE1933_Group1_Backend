// Author: Vu Quoc Hoang - HE194781
package com.se1933g01.steam_clone_backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MultipartFilter;

import com.se1933g01.steam_clone_backend.dto.AddingGameRequestDTO;
import com.se1933g01.steam_clone_backend.entity.request.AddingGameRequest;
import com.se1933g01.steam_clone_backend.entity.request.Request;
import com.se1933g01.steam_clone_backend.entity.user.User;
import com.se1933g01.steam_clone_backend.repository.AddingGameRequestRepo;
import com.se1933g01.steam_clone_backend.repository.RequestRepo;
import com.se1933g01.steam_clone_backend.repository.UserRepo;

import jakarta.transaction.Transactional;

@Service
public class PublisherService {
    @Autowired
    private AddingGameRequestRepo addingGameRequestRepo;
    @Autowired
    private RequestRepo requestRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserRepo userRepo;
    @Transactional
    public void addGame(AddingGameRequestDTO addingGameRequestDTO, Long UserID) {
        User user = userRepo.findById(UserID).orElseThrow(()-> new RuntimeException("User not found"));
        Request request = new Request();
        request.setUser(user);
        request.setRequestType("Game Submission");
        request.setTimeCreated(LocalDate.now());
        request.setRequestState(0);
        Request savedRequest = requestRepo.save(request); 
        AddingGameRequest addingGameRequest = modelMapper.map(addingGameRequestDTO, AddingGameRequest.class);
        System.out.println(addingGameRequest.getMediaUrls());
        addingGameRequest.setReleaseDate(LocalDate.now());
        addingGameRequest.setRequest(savedRequest);
        addingGameRequestRepo.save(addingGameRequest);
    }

}
