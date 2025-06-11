package com.se1933g01.steam_clone_backend.repository;

import com.se1933g01.steam_clone_backend.entity.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {
    // Role findByType(String type);
}