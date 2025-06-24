package com.se1933g01.steamclonebackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.se1933g01.steamclonebackend.entity.user.Notification;

import java.util.List;


public interface NotificationsRepo extends JpaRepository<Notification, Long> {
    List<Notification> findByUser_userId(Long userId);
}
