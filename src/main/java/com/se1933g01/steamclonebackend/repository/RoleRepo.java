package com.se1933g01.steamclonebackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.se1933g01.steamclonebackend.entity.user.Role;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {
}