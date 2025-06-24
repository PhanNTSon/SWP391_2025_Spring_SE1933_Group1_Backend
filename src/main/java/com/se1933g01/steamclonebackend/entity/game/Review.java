package com.se1933g01.steamclonebackend.entity.game;

import java.time.LocalDate;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.se1933g01.steamclonebackend.entity.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Author: Phan Son
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    // ================ Relationships =============
    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "UserID")
    private User user;

    @ManyToOne
    @MapsId("gameId")
    @JoinColumn(name = "GameID")
    private Game game;

    @ManyToMany(mappedBy = "likedReviews")
    @JsonIgnore
    private Set<User> likedByUsers;

    @ManyToMany(mappedBy = "unlikedReviews")
    @JsonIgnore
    private Set<User> unlikedByUsers;

    
}

