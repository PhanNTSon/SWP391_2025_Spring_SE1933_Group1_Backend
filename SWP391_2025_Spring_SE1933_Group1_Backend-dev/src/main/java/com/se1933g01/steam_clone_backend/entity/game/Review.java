package com.se1933g01.steam_clone_backend.entity.game;

import java.time.LocalDate;

import com.se1933g01.steam_clone_backend.entity.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "Review")
public class Review {
    @EmbeddedId
    private ReviewKey id;

    @Column(name = "ReviewContent")
    private String reviewContent;

    @Column(name = "TimeCreated")
    private LocalDate timeCreated;

    @Column(name = "IsRecommended")
    private boolean isRecommended;

    @Column(name = "Helpful")
    private long helpful;

    @Column(name = "NotHelpful")
    private long notHelpful;

    // ================ Relationships =============
    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "UserID")
    private User user;

    @ManyToOne
    @MapsId("gameId")
    @JoinColumn(name = "GameID")
    private Game game;

    public Review() {
    }

    public Review(ReviewKey id, String reviewContent, LocalDate timeCreated, boolean isRecommended, long helpful,
            long notHelpful, User user, Game game) {
        this.id = id;
        this.reviewContent = reviewContent;
        this.timeCreated = timeCreated;
        this.isRecommended = isRecommended;
        this.helpful = helpful;
        this.notHelpful = notHelpful;
        this.user = user;
        this.game = game;
    }

    public ReviewKey getId() {
        return id;
    }

    public void setId(ReviewKey id) {
        this.id = id;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    public LocalDate getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(LocalDate timeCreated) {
        this.timeCreated = timeCreated;
    }

    public boolean isRecommended() {
        return isRecommended;
    }

    public void setRecommended(boolean isRecommended) {
        this.isRecommended = isRecommended;
    }

    public long getHelpful() {
        return helpful;
    }

    public void setHelpful(long helpful) {
        this.helpful = helpful;
    }

    public long getNotHelpful() {
        return notHelpful;
    }

    public void setNotHelpful(long notHelpful) {
        this.notHelpful = notHelpful;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
    
    
}
