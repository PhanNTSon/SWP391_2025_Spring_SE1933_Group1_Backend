package com.se1933g01.steam_clone_backend.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.lang.Long;
import com.se1933g01.steam_clone_backend.entity.user.User;
import com.se1933g01.steam_clone_backend.repository.UserRepo;

/**
 * Author: Phan son
 */
@Service
public class UserService {
    private final UserRepo userRepo;
    @Autowired
    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }
    
    public User getUser(Long id){
        return userRepo.findById(id).orElse(null);
    }

    
}
