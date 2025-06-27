package com.se1933g01.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.se1933g01.steamclonebackend.dto.notification.NotificationDTO;
import com.se1933g01.steamclonebackend.entity.user.Notification;
import com.se1933g01.steamclonebackend.repository.NotificationsRepo;
import com.se1933g01.steamclonebackend.service.NotificationService;

import jakarta.persistence.EntityManager;

public class NotificationServiceTest {
    private EntityManager entityManager;
    private NotificationsRepo notificationsRepo;

    private NotificationService service;

    @Before
    public void setup() {
        entityManager = mock(EntityManager.class);
        notificationsRepo = mock(NotificationsRepo.class);

        service = new NotificationService(entityManager, notificationsRepo);
    }

    @Test
    public void getNotificationsList_validUserId_returnsCorrectList() {
        long userId = 1;
        service.getNotificationsList(userId);
        Notification notif = new Notification();
        notif.setNotificationId(100L);
        notif.setNotificationType("Cart");
        notif.setNotificationContent("Game add success");
        notif.setRead(false);

        when(notificationsRepo.findByUser_userId(userId)).thenReturn(Arrays.asList(notif));

        // Get the result from service with above mock data
        List<NotificationDTO> result = service.getNotificationsList(userId);

        // Assert - (compare the resutl with expected)
        assertEquals(1, result.size());

        NotificationDTO dto = result.get(0);
        assertEquals(100L, dto.getNotifId());
        assertEquals("Cart", dto.getNotificationType());
        assertEquals("Game add success", dto.getNotificationContent());
        assertFalse(dto.isRead());
    }

}
