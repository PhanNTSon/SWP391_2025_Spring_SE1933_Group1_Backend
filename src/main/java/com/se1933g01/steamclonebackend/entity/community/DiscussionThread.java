package com.se1933g01.steamclonebackend.entity.community;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.se1933g01.steamclonebackend.entity.game.Game;
import com.se1933g01.steamclonebackend.entity.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "DiscussionThread")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class DiscussionThread {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long threadId;

    @Column(nullable = false)
    private String title;

    @Column()
    private String content;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "UserID", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "GameID")
    private Game game;

    @OneToMany(mappedBy = "thread", cascade = CascadeType.ALL)
    private List<DiscussionComment> comments = new ArrayList<>();
}

