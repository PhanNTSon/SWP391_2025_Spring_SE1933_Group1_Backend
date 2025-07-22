package com.se1933g01.steamclonebackend.entity.game;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.se1933g01.steamclonebackend.entity.user.Library;
import com.se1933g01.steamclonebackend.entity.user.Publisher;
import com.se1933g01.steamclonebackend.entity.user.User;

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
@Table(name = "Game", schema = "public")
public class Game {
    @Id
    @Column(name = "GameID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;

    @Column(name = "Name", length = 100, nullable = false)
    private String name;

    @Column(name = "ReleaseDate")
    private LocalDate releaseDate;

    @Column(name = "State")
    private Boolean state;

    @Column(name = "Price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "ShortDescription")
    private String shortDescription;

    @Column(name = "FullDescription", columnDefinition = "TEXT")
    private String fullDescription;

    @Column(name = "TotalPurchased")
    private Integer totalPurchased;

    @Column(name = "OS", length = 50)
    private String os;

    @Column(name = "Storage", length = 50)
    private String storage;

    @Column(name = "Processor", length = 50)
    private String processor;

    @Column(name = "Memory", length = 50)
    private String memory;

    @Column(name = "AdditionalNotes", columnDefinition = "TEXT")
    private String additionalNotes;

    @Column(name = "Graphics", length = 50)
    private String graphics;

    @Column(name = "GameUrl")
    private String gameUrl;

    @Column(name = "IconUrl")
    private String iconUrl;

    @Column(name = "UpdateLog")
    private String updateLog;

    // ================ Relationships =============
    @ManyToOne
    @JoinColumn(name = "PublisherID")
    private Publisher publisher;

    @ManyToMany
    @JoinTable(name = "GameTags", joinColumns = @JoinColumn(name = "GameID"), inverseJoinColumns = @JoinColumn(name = "TagID"))
    private Set<Tag> tags;

    // @OneToMany(mappedBy = "game")
    // private List<TransactionDetail> transactionDetails;

    @OneToMany(mappedBy = "game")
    private Set<Library> libraryGames = new HashSet<>();

    @ManyToMany(mappedBy = "cartGames")
    @JsonIgnore
    private Set<User> usersInCart = new HashSet<>();

    // @ManyToMany(mappedBy = "games")
    // private Set<User> users;

    @OneToMany(mappedBy = "game")
    private List<Review> reviews;

    @OneToMany(mappedBy = "game")
    private List<Media> media;

    // ================ Getter & Setter =============
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Game game = (Game) o;
        return gameId != null && gameId.equals(game.gameId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(gameId);
    }
}
