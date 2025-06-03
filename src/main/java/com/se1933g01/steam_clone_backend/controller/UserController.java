package com.se1933g01.steam_clone_backend.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.se1933g01.steam_clone_backend.entity.user.User;
import com.se1933g01.steam_clone_backend.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @Autowired
    @GetMapping("/users")
    public User getAllUsers() {
        Long userId = 1L;
        return userService.getUser(userId);
    }
}
