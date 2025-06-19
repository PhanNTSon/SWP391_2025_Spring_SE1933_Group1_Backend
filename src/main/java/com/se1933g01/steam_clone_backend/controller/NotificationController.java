package com.se1933g01.steam_clone_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.se1933g01.steam_clone_backend.dto.Notification.CreateNotificationDTO;
import com.se1933g01.steam_clone_backend.dto.Notification.NotificationDTO;
import com.se1933g01.steam_clone_backend.service.NotificationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Phan NT Son
 */
@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notifService;

    /**
     * Get list of all Notifications
     * 
     * @param userIdLong
     * @return
     */
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<List<NotificationDTO>> getNotificationList(@RequestParam Long userId) {
        List<NotificationDTO> result = notifService.getNotificationsList(userId);
        return ResponseEntity.ok(result);
    }

    /**
     * Get list of Unread Notifications
     * 
     * @param userId
     * @return
     */
    @GetMapping("/list/unread")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotificationList(@RequestParam Long userId) {
        List<NotificationDTO> result = notifService.getUnreadNotifList(userId);
        return ResponseEntity.ok(result);
    }

    /**
     * Create new Notification
     * 
     * @param dto
     * @return
     */
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<CreateNotificationDTO> createNotificatioin(@RequestBody CreateNotificationDTO dto) {
        CreateNotificationDTO result = notifService.createNotif(dto.getUserId(), dto.getNotificationType(),
                dto.getNotificationContent());
        return ResponseEntity.ok(result);
    }

    /**
     * Update a notification from unread to read
     * 
     * @param notificationId
     * @return
     */
    @PatchMapping("/markread/{notifId}")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<Void> patchNotif(@PathVariable(name = "notifId") long notificationId) {
        notifService.patchNotif(notificationId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Delete a notifcation
     * @param notificationId
     * @return
     */
    @DeleteMapping("/delete/{notifId}")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<Void> deleteNotif(@PathVariable(name = "notifId") long notificationId) {
        notifService.deleteNotif(notificationId);
        return ResponseEntity.noContent().build();
    }
}
