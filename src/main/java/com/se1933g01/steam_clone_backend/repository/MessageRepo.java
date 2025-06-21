package com.se1933g01.steam_clone_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.se1933g01.steam_clone_backend.entity.community.Message;

/**
 * @author Phan NT Son
 * @since 21-06-2025
 */
@Repository
public interface MessageRepo extends JpaRepository<Message, Long> {

}
