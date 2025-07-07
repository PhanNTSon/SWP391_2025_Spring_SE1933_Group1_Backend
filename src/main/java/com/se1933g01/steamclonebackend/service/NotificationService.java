package com.se1933g01.steamclonebackend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.se1933g01.steamclonebackend.dto.notification.CreateNotificationDTO;
import com.se1933g01.steamclonebackend.dto.notification.NotificationDTO;
import com.se1933g01.steamclonebackend.entity.user.Notification;
import com.se1933g01.steamclonebackend.entity.user.User;
import com.se1933g01.steamclonebackend.repository.NotificationsRepo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;

/**
 * @author Phan NT Son
 */
@Service
public class NotificationService {

    private final EntityManager entityManager;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationsRepo notificationsRepo;
    private final String SOCKET_NOTIFICATIONS_ALL = "/queue/notification.all";
    private final String SOCKET_NOTIFICATIONS_UNREAD = "/queue/notification.unread";

    public NotificationService(EntityManager entityManager, SimpMessagingTemplate simpMessagingTemplate,
            NotificationsRepo notificationsRepo) {
        this.entityManager = entityManager;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.notificationsRepo = notificationsRepo;
    }

    private void sendSocketAllNotifications(String username, long userId) {
        simpMessagingTemplate.convertAndSendToUser(username, SOCKET_NOTIFICATIONS_ALL, getNotificationsList(userId));
    }

    private void sendSocketUnreadNotification(String username, long userId, Notification newNotif) {

        NotificationDTO n = new NotificationDTO(newNotif.getNotificationId(), newNotif.getNotificationType(),
                newNotif.getNotificationContent(), newNotif.isRead());

        simpMessagingTemplate.convertAndSendToUser(username, SOCKET_NOTIFICATIONS_UNREAD, n);

    }

    /**
     * Get list of Notifications
     * 
     * @param userId
     * @return
     */
    public List<NotificationDTO> getNotificationsList(Long userId) {
        List<Notification> resultFromDB = notificationsRepo.findByUser_userId(userId);

        List<NotificationDTO> resultListDtos = new ArrayList<>();

        resultFromDB.stream()
                .forEach(notif -> {
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
     * Get a list of unread Notifications of a User based on their ID
     * 
     * @param userId
     * @return
     */
    public List<NotificationDTO> getUnreadNotifList(Long userId) {
        List<Notification> resultFromDB = notificationsRepo.getUnreadList(userId);

        List<NotificationDTO> resultListDtos = new ArrayList<>();

        resultFromDB.stream()
                .forEach(notif -> {
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
     * Create new Notification to a person based on their ID
     * which are receiverId.
     * 
     * @param receiverId
     * @param notifType
     * @param notifContent
     * @return CreateNotificationDTO with receiverName for real-time notification by
     *         Socket
     */
    @Transactional
    public CreateNotificationDTO createNotif(long receiverId, String notifType, String notifContent) {
        User user = entityManager.getReference(User.class, receiverId);

        Notification newNotif = new Notification();
        newNotif.setUser(user);
        newNotif.setNotificationType(notifType);
        newNotif.setNotificationContent(notifContent);
        newNotif.setRead(false);

        Notification saved = notificationsRepo.save(newNotif);

        sendSocketAllNotifications(saved.getUser().getUsername(), saved.getUser().getUserId());
        sendSocketUnreadNotification(saved.getUser().getUsername(), saved.getUser().getUserId(), saved);

        CreateNotificationDTO returnNotif = new CreateNotificationDTO();
        returnNotif.setNotificationContent(notifContent);
        returnNotif.setNotificationType(notifType);
        returnNotif.setReceiverId(receiverId);

        return returnNotif;
    }

    /**
     * Set a notification status from Not-read to have-Read.
     * 
     * @param notifId
     */
    @Transactional
    public void patchANotif(long notifId) {
        Notification target = notificationsRepo.findById(notifId).orElseThrow();
        target.setRead(true);
        notificationsRepo.save(target);

        // Send full list of notification to /user/queue/notification.all
        sendSocketAllNotifications(target.getUser().getUsername(), target.getUser().getUserId());
    }

    @Transactional
    public void patchAllNotifOfUser(long userId) {
        notificationsRepo.markAllRead(userId);

        User user = entityManager.getReference(User.class, userId);
        sendSocketAllNotifications(user.getUsername(), userId);
    }

    @Transactional
    public void deleteNotif(long notifId) {
        Notification target = notificationsRepo.findById(notifId)
                .orElseThrow(() -> new EntityNotFoundException("Notif not found"));
        notificationsRepo.delete(target);

        sendSocketAllNotifications(target.getUser().getUsername(), target.getUser().getUserId());

    }

    @Transactional
    public void deleteAllByUserId(long userId) {
        notificationsRepo.deleteAllByUserId(userId);

        User user = entityManager.getReference(User.class, userId);
        sendSocketAllNotifications(user.getUsername(), userId);
    }
}
