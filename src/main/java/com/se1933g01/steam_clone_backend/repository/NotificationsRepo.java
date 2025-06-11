package com.se1933g01.steam_clone_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.se1933g01.steam_clone_backend.entity.user.Notification;
import java.util.List;


public interface NotificationsRepo extends JpaRepository<Notification, Long> {
    List<Notification> findByUser_userID(Long userId);
}
