package com.se1933g01.steamclonebackend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.se1933g01.steamclonebackend.dto.GameDetailDTO;
import com.se1933g01.steamclonebackend.dto.LibraryDTO;
import com.se1933g01.steamclonebackend.dto.MediaDTO;
import com.se1933g01.steamclonebackend.dto.PublisherBasicDTO;
import com.se1933g01.steamclonebackend.dto.TagDTO;
import com.se1933g01.steamclonebackend.entity.game.Game;
import com.se1933g01.steamclonebackend.entity.user.User;
import com.se1933g01.steamclonebackend.repository.UserRepo;

import jakarta.persistence.EntityNotFoundException;
@Service
public class LibraryService {

    private UserRepo userRepo;
    private static final String USER_NOT_FOUND_MSG = "User not found";

    public LibraryService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }
    /**
     * Show library of a user.
     * 
     * @author Ba Thanh
     * @param userId
     * @return LibraryDTO containing user's library
     */
    public LibraryDTO showLibrary(Long userId) {
        User user = userRepo.findByIdWithLibraryGames(userId);
        if (user == null)
            throw new EntityNotFoundException(USER_NOT_FOUND_MSG);
        LibraryDTO libraryDTO = new LibraryDTO();
        List<GameDetailDTO> gameDetailList = new ArrayList<>();
        for (Game game : user.getGames()) {
            GameDetailDTO gameInLibrary = new GameDetailDTO();
            // add game detail
            gameInLibrary.setGameId(game.getGameId());
            gameInLibrary.setPublisher(new PublisherBasicDTO(game.getPublisher().getPublisherId(),
                    game.getPublisher().getPublisherName()));
            gameInLibrary.setName(game.getName());
            gameInLibrary.setReleaseDate(game.getReleaseDate());
            gameInLibrary.setState(game.getState());
            gameInLibrary.setPrice(game.getPrice());
            gameInLibrary.setShortDescription(game.getShortDescription());
            gameInLibrary.setFullDescription(game.getFullDescription());
            gameInLibrary.setTotalPurchased(game.getTotalPurchased());
            gameInLibrary.setTags(game.getTags().stream()
                    .map(tag -> new TagDTO(tag.getTagId(), tag.getTagName()))
                    .collect(Collectors.toSet()));
            gameInLibrary.setMedia(game.getMedia().stream()
                    .map(media -> new MediaDTO(media.getMediaId(), media.getUrl(), media.getType()))
                    .toList());
            gameInLibrary.setOs(game.getOs());
            gameInLibrary.setStorage(game.getStorage());
            gameInLibrary.setProcessor(game.getProcessor());
            gameInLibrary.setMemory(game.getMemory());
            gameInLibrary.setAdditionalNotes(game.getAdditionalNotes());
            gameInLibrary.setGraphics(game.getGraphics());
            // add gamedetail into list
            gameDetailList.add(gameInLibrary);
        }
        libraryDTO.setUserId(userId);
        libraryDTO.setLibrary(gameDetailList);
        return libraryDTO;
    }
}
