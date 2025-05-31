package com.se1933g01.steam_clone_backend.entity.game;

import java.sql.Date;
import java.util.List;
import java.util.Set;

import com.se1933g01.steam_clone_backend.entity.transaction.TransactionDetail;
import com.se1933g01.steam_clone_backend.entity.user.Publisher;
import com.se1933g01.steam_clone_backend.entity.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Game {
    @Id
    @Column(name = "GameID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;

    @Column(name = "Name")
    private String name;

    @Column(name = "ReleaseDate")
    private Date releaseDate;

    @Column(name = "State")
    private boolean state;

    @Column(name = "Price")
    private double price;

    @Column(name = "ShortDescription")
    private String shortDescription;

    @Column(name = "FullDescription")
    private String fullDescription;

    @Column(name = "TotalPurchased")
    private int totalPurchased;

    // ================ Relationships =============
    @ManyToOne
    @JoinColumn(name = "PublisherID")
    private Publisher publisher;

    @ManyToMany
    @JoinTable(
        name = "GameTags",
        joinColumns = @JoinColumn(name = "GameID"),
        inverseJoinColumns = @JoinColumn(name = "TagID")
    )
    private Set<Tag> tags;

    @OneToMany(mappedBy = "game")
    private List<TransactionDetail> transactionDetails;

    @ManyToMany(mappedBy = "cartGames")
    private Set<User> cartUsers;

    @ManyToMany(mappedBy = "games")
    private Set<User> users;

    @OneToMany(mappedBy = "game")
    private List<Review> reviews;

    @OneToMany(mappedBy = "game")
    private List<Media> medias;

    @OneToOne(mappedBy = "game")
    private SystemRequirement systemRequirement;
    // ================ Getter & Setter =============

}
