package com.se1933g01.steam_clone_backend.entity.game;

import java.time.LocalDate;

import com.se1933g01.steam_clone_backend.entity.CompositedKey;
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
    private CompositedKey id;

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
    @MapsId("key1")
    @JoinColumn(name = "UserID")
    private User user;

    @ManyToOne
    @MapsId("key2")
    @JoinColumn(name = "GameID")
    private Game game;
    
}
