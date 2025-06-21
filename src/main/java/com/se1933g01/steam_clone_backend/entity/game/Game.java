package com.se1933g01.steam_clone_backend.entity.game;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
// import com.se1933g01.steam_clone_backend.entity.transaction.TransactionDetail;
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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: Phan Son
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Game")
public class Game {
    @Id
    @Column(name = "GameID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;

    @Column(name = "Name")
    private String name;

    @Column(name = "ReleaseDate")
    private LocalDate releaseDate;

    @Column(name = "State")
    private Boolean state;

    @Column(name = "Price")
    private BigDecimal price;

    @Column(name = "ShortDescription", columnDefinition = "NVARCHAR(MAX)")
    private String shortDescription;

    @Column(name = "FullDescription", columnDefinition = "NVARCHAR(MAX)")
    private String fullDescription;

    @Column(name = "TotalPurchased")
    private int totalPurchased;

    @Column(name = "Os")
    private String os;
    @Column(name = "Storage")
    private String storage;
    @Column(name = "Processor")
    private String processor;
    @Column(name = "Memory")
    private String memory;
    @Column(name = "AdditionalNotes")
    private String additionalNotes;
    @Column(name = "Graphics")
    private String graphics;
    // ================ Relationships =============
    @ManyToOne
    @JoinColumn(name = "PublisherID")
    private Publisher publisher;

    @ManyToMany
    @JoinTable(name = "GameTags", joinColumns = @JoinColumn(name = "GameID"), inverseJoinColumns = @JoinColumn(name = "TagID"))
    private Set<Tag> tags;

    // @OneToMany(mappedBy = "game")
    // private List<TransactionDetail> transactionDetails;

    @ManyToMany(mappedBy = "cartGames")
    @JsonIgnore
    private Set<User> usersInCart = new HashSet<>();

    @ManyToMany(mappedBy = "games")
    private Set<User> users;

    // @OneToMany(mappedBy = "game")
    // private List<Review> reviews;

    @OneToMany(mappedBy = "game")
    private List<Media> media;

    // ================ Getter & Setter =============
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return gameId != null && gameId.equals(game.gameId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(gameId);
    }
}
