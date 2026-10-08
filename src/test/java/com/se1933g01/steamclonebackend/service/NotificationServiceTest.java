package com.se1933g01.steamclonebackend.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.se1933g01.steamclonebackend.dto.notification.NotificationDTO;
import com.se1933g01.steamclonebackend.entity.user.Notification;
import com.se1933g01.steamclonebackend.repository.NotificationsRepo;

import jakarta.persistence.EntityManager;

public class NotificationServiceTest {
    private EntityManager entityManager;
    private NotificationsRepo notificationsRepo;
    private SimpMessagingTemplate simpMessagingTemplate;

    private NotificationService service;

    @Before
    public void setup() {
        entityManager = mock(EntityManager.class);
        notificationsRepo = mock(NotificationsRepo.class);
        simpMessagingTemplate = mock(SimpMessagingTemplate.class);

        service = new NotificationService(entityManager, simpMessagingTemplate, notificationsRepo);
    }

    @Test
    public void getNotificationsList_validUserId_returnsCorrectList() {
        long userId = 1;
        Notification notif = new Notification();
        notif.setNotificationId(1L);
        notif.setNotificationType("Cart");
        notif.setNotificationContent("Game add success");
        notif.setRead(false);

        Notification notif2 = new Notification();
        notif2.setNotificationId(2L);
        notif2.setNotificationType("Comunity");
        notif2.setNotificationContent("Game add success");
        notif2.setRead(true);

        List<Notification> returnFromDB = Arrays.asList(notif, notif2);

        when(notificationsRepo.findByUser_userId(userId)).thenReturn(returnFromDB);

        // Get the result from service with above mock data
        List<NotificationDTO> result = service.getNotificationsList(userId);

        // Assert - (compare the resutl with expected)
        assertEquals(2, result.size());

        NotificationDTO dto1 = result.get(0);
        assertEquals(1L, dto1.getNotifId());
        assertEquals("Cart", dto1.getNotificationType());
        assertEquals("Game add success", dto1.getNotificationContent());
        assertFalse(dto1.isRead());

        NotificationDTO dto2 = result.get(1);
        assertEquals(2L, dto2.getNotifId());
        assertEquals("Comunity", dto2.getNotificationType());
        assertEquals("Game add success", dto2.getNotificationContent());
        assertTrue(dto2.isRead());

    }

}
