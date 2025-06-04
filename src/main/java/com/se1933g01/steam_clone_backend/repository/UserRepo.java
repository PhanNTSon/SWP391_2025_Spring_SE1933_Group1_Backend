package com.se1933g01.steam_clone_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.se1933g01.steam_clone_backend.entity.user.User;

@Repository
public interface UserRepo extends JpaRepository<User,Long>{
    
}
