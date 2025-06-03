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

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }

    public int getTotalPurchased() {
        return totalPurchased;
    }

    public void setTotalPurchased(int totalPurchased) {
        this.totalPurchased = totalPurchased;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public List<TransactionDetail> getTransactionDetails() {
        return transactionDetails;
    }

    public void setTransactionDetails(List<TransactionDetail> transactionDetails) {
        this.transactionDetails = transactionDetails;
    }

    public Set<User> getCartUsers() {
        return cartUsers;
    }

    public void setCartUsers(Set<User> cartUsers) {
        this.cartUsers = cartUsers;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<Media> getMedias() {
        return medias;
    }

    public void setMedias(List<Media> medias) {
        this.medias = medias;
    }

    public SystemRequirement getSystemRequirement() {
        return systemRequirement;
    }

    public void setSystemRequirement(SystemRequirement systemRequirement) {
        this.systemRequirement = systemRequirement;
    }

    
}
