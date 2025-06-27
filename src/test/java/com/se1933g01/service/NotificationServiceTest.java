package com.se1933g01.service;

import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

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
    }

}
