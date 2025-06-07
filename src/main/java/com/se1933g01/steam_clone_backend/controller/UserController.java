package com.se1933g01.steam_clone_backend.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.se1933g01.steam_clone_backend.entity.user.User;
import com.se1933g01.steam_clone_backend.service.GoogleDriveService;
import com.se1933g01.steam_clone_backend.service.UserService;

/**
 * Author: Phan son
 */

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    @Autowired
    private GoogleDriveService googleDriveService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // @Autowired
    @GetMapping("/users")
    public User getAllUsers() {
        Long userId = 1L;
        return userService.getUser(userId);
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<String> downloadFile(@PathVariable("fileId") String fileId) throws IOException {
        String downloadUrl = googleDriveService.generateDownloadUrl(fileId);
        System.out.println(downloadUrl);
        return ResponseEntity.ok(downloadUrl);
    }
}
