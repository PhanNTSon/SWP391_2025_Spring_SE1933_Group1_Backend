package com.se1933g01.steam_clone_backend.repository;

import com.se1933g01.steam_clone_backend.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepo extends JpaRepository<User, Long> {
    
}