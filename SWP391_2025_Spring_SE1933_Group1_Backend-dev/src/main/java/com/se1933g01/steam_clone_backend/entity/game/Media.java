package com.se1933g01.steam_clone_backend.entity.game;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Media")
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long mediaId;

    @Column(name = "Url")
    private String url;

    @Column(name = "Type")
    private String type;

    // ================ Relationships =============
    @ManyToOne
    @JoinColumn(name = "GameID")
    private Game game;

}
