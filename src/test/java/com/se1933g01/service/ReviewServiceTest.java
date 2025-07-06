package com.se1933g01.service;

import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.se1933g01.steamclonebackend.repository.GameRepo;
import com.se1933g01.steamclonebackend.repository.ReviewRepo;
import com.se1933g01.steamclonebackend.repository.UserRepo;
import com.se1933g01.steamclonebackend.service.ReviewService;

import jakarta.persistence.EntityManager;

public class ReviewServiceTest {

    private EntityManager entityManager;
    private GameRepo gameRepo;
    private UserRepo userRepo;
    private ReviewRepo reviewRepo;
    private SimpMessagingTemplate simp;

    private ReviewService service;

    @Before
    public void setup() {
        entityManager = mock(EntityManager.class);
        gameRepo = mock(GameRepo.class);
        userRepo = mock(UserRepo.class);
        reviewRepo = mock(ReviewRepo.class);
        simp = mock(SimpMessagingTemplate.class);

        service = new ReviewService(entityManager, gameRepo, userRepo, reviewRepo, simp);
    }

    

}
