package com.se1933g01.steamclonebackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.se1933g01.steamclonebackend.dto.review.CreateReviewDTO;
import com.se1933g01.steamclonebackend.entity.game.Game;
import com.se1933g01.steamclonebackend.entity.game.Review;
import com.se1933g01.steamclonebackend.entity.user.User;
import com.se1933g01.steamclonebackend.repository.GameRepo;
import com.se1933g01.steamclonebackend.repository.ReviewRepo;
import com.se1933g01.steamclonebackend.repository.UserRepo;
import com.se1933g01.steamclonebackend.service.ReviewService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;

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

    @Test
    public void testCheckUserReaction() {

    }

    // UTCUID 01
    @Test
    public void testCreateReview_success_true() {
        // Arrange
        Long userId = 1L;
        Long gameId = 1L;
        String content = "nice";
        boolean isRecommended = true;

        User user = new User();
        user.setUserId(userId);

        Game game = new Game();
        game.setGameId(gameId);

        when(entityManager.getReference(User.class, userId)).thenReturn(user);
        when(entityManager.getReference(Game.class, gameId)).thenReturn(game);
        when(reviewRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CreateReviewDTO result = service.createReview(userId, gameId, content, isRecommended);

        // Assert
        assertNotNull(result);
        assertEquals(gameId, result.getGameId());
        assertEquals(content, result.getReviewContent());
        assertTrue(result.isRecommended());

        verify(reviewRepo).save(any(Review.class));
    }

    // UTCUID 08
    @Test
    public void testCreateReview_success_false() {

        Long userId = 1L;
        Long gameId = 1L;
        String content = "nice";
        boolean isRecommended = false;

        User user = new User();
        user.setUserId(userId);

        Game game = new Game();
        game.setGameId(gameId);

        when(entityManager.getReference(User.class, 1L)).thenReturn(user);
        when(entityManager.getReference(Game.class, 1L)).thenReturn(game);
        when(reviewRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CreateReviewDTO result = service.createReview(userId, gameId, content, isRecommended);

        assertEquals(gameId, result.getGameId());
        assertEquals(content, result.getReviewContent());
        assertFalse(result.isRecommended());
    }

    // UTCUID 03
    @Test
    public void testCreateReview_nullUserId() {
        assertThrows(IllegalArgumentException.class, () -> service.createReview(null, 1L, "nice", true));
    }

    // UTCUID 02
    @Test
    public void testCreateReview_userNotFound() {
        when(entityManager.getReference(User.class, 3L)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> service.createReview(3L, 1L, "nice", true));
    }

    // UTCUID 04
    @Test
    public void testCreateReview_gameNotFound() {
        when(entityManager.getReference(Game.class, 3L)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> service.createReview(1L, 3L, "nice", true));
    }

    // UTCUID 05
    @Test
    public void testCreateReview_nullGameId() {
        assertThrows(IllegalArgumentException.class, () -> service.createReview(1L, null, "nice", true));
    }

    // UTCUID 06
    @Test
    public void testCreateReview_blankReviewContent() {
        assertThrows(IllegalArgumentException.class, () -> service.createReview(1L, 1L, "", true));
    }

    // UTCUID 07
    @Test
    public void testCreateReview_nullReviewContent() {
        assertThrows(IllegalArgumentException.class, () -> service.createReview(1L, 1L, null, true));
    }

    // UTCUID 09
    @Test
    public void testCreateReview_nullIsRecommend() {
        assertThrows(IllegalArgumentException.class, () -> service.createReview(1L, 1L, "nice", null));
    }

    @Test
    public void testDeleteReactionReview() {

    }

    @Test
    public void testDeleteReview() {

    }

    @Test
    public void testGetGameReviewList() {

    }

    @Test
    public void testPatchLikeReview() {

    }

    @Test
    public void testPatchUnLikeReview() {

    }

    @Test
    public void testUpdateReview() {

    }
}
