package com.se1933g01.steamclonebackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.se1933g01.steamclonebackend.entity.user.Notification;

import java.util.List;

@Repository
public interface NotificationsRepo extends JpaRepository<Notification, Long> {
    List<Notification> findByUser_userId(Long userId);

    @Query("SELECT n FROM Notification n WHERE n.user.userId = :userId AND n.read = false")
    List<Notification> getUnreadList (@Param("userId") long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.read = true WHERE n.user.userId = :userId")
    void markAllRead(@Param("userId") long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.user.userId = :userId")
    void deleteAllByUserId(@Param("userId") long userId);
}
