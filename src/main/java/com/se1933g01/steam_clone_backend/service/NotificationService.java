package com.se1933g01.steam_clone_backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.se1933g01.steam_clone_backend.dto.Notification.CreateNotificationDTO;
import com.se1933g01.steam_clone_backend.dto.Notification.NotificationDTO;
import com.se1933g01.steam_clone_backend.entity.user.Notification;
import com.se1933g01.steam_clone_backend.entity.user.User;
import com.se1933g01.steam_clone_backend.repository.NotificationsRepo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;

/**
 * @author Phan NT Son
 */
@Service
public class NotificationService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private NotificationsRepo notificationsRepo;

    /**
     * Get list of Notifications
     * 
     * @param userId
     * @return
     */
    public List<NotificationDTO> getNotificationsList(Long userId) {
        List<Notification> resultFromDB = notificationsRepo.findByUser_userID(userId);
        List<NotificationDTO> resultListDtos = new ArrayList<>();
        resultFromDB.stream().forEach(notif -> {
            NotificationDTO dto = new NotificationDTO();
            dto.setNotifId(notif.getNotificationId());
            dto.setNotificationContent(notif.getNotificationContent());
            dto.setNotificationType(notif.getNotificationType());
            dto.setRead(notif.isRead());

            resultListDtos.add(dto);
        });
        return resultListDtos;
    }

    /**
     * Get a list of unread Notifications
     * 
     * @param userId
     * @return
     */
    public List<NotificationDTO> getUnreadNotifList(Long userId) {
        List<Notification> resultFromDB = notificationsRepo.findByUser_userID(userId);
        List<NotificationDTO> resultListDtos = new ArrayList<>();
        resultFromDB.stream().forEach(notif -> {
            // If notification is not read
            if (!notif.isRead()) {
                NotificationDTO dto = new NotificationDTO();
                dto.setNotifId(notif.getNotificationId());
                dto.setNotificationContent(notif.getNotificationContent());
                dto.setNotificationType(notif.getNotificationType());
                dto.setRead(notif.isRead());

                resultListDtos.add(dto);
            }
        });
        return resultListDtos;
    }

    /**
     * Create new Notification
     * 
     * @param userId
     * @param notifType
     * @param notifContent
     * @return
     */
    @Transactional
    public CreateNotificationDTO createNotif(long userId, String notifType, String notifContent) {
        User user = entityManager.getReference(User.class, userId);

        Notification newNotif = new Notification();
        newNotif.setUser(user);
        newNotif.setNotificationType(notifType);
        newNotif.setNotificationContent(notifContent);
        newNotif.setRead(false);

        notificationsRepo.save(newNotif);

        CreateNotificationDTO returnNotif = new CreateNotificationDTO();
        returnNotif.setNotificationContent(notifContent);
        returnNotif.setNotificationType(notifType);
        returnNotif.setUserId(userId);
        return returnNotif;
    }

    @Transactional
    public void patchNotif(long notifId) {
        Notification target = notificationsRepo.findById(notifId).orElseThrow();
        target.setRead(true);
        notificationsRepo.save(target);
    }

    @Transactional
    public void deleteNotif(long notifId) {
        Notification target = notificationsRepo.findById(notifId)
                .orElseThrow(() -> new EntityNotFoundException("Notif not found"));
        notificationsRepo.delete(target);
    }
}
