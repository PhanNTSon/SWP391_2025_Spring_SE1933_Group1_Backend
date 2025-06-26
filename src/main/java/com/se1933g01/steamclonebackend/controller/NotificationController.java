package com.se1933g01.steamclonebackend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.se1933g01.steamclonebackend.dto.notification.CreateNotificationDTO;
import com.se1933g01.steamclonebackend.dto.notification.NotificationDTO;
import com.se1933g01.steamclonebackend.entity.user.CustomUserDetail;
import com.se1933g01.steamclonebackend.service.NotificationService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Phan NT Son
 */
@RestController
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notifService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public NotificationController(NotificationService notifService, SimpMessagingTemplate simpMessagingTemplate) {
        this.notifService = notifService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    /**
     * Get list of all Notifications
     * 
     * @param userIdLong
     * @return
     */
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('STANDARD','PUBLISHER','ADMIN')")
    public ResponseEntity<List<NotificationDTO>> getNotificationList(@AuthenticationPrincipal CustomUserDetail me) {
        List<NotificationDTO> result = notifService.getNotificationsList(me.getUser().getUserId());
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
    public ResponseEntity<List<NotificationDTO>> getUnreadNotificationList(
            @AuthenticationPrincipal CustomUserDetail me) {
        List<NotificationDTO> result = notifService.getUnreadNotifList(me.getUser().getUserId());
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
        CreateNotificationDTO result = notifService.createNotif(dto.getReceiverId(), dto.getNotificationType(),
                dto.getNotificationContent());
                
        simpMessagingTemplate.convertAndSendToUser(result.getReceiverName(), "/queue/notifications", result);
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
     * 
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
