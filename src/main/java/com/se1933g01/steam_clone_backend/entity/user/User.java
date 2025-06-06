package com.se1933g01.steam_clone_backend.entity.user;

import java.sql.Date;
import java.util.List;
import java.util.Set;

import com.se1933g01.steam_clone_backend.entity.game.Game;
import com.se1933g01.steam_clone_backend.entity.request.Request;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.Table;

@Entity
@Table(name = "User")
public class User {

    @Id
    @Column(name = "UserID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userID;

    @Column(name = "Email")
    private String email;

    @Column(name = "Username")
    private String username;

    @Column(name = "Password")
    private String password;

    @Column(name = "WalletBalance")
    private Double walletBalance;

    @Column(name = "Country")
    private String country;

    @Column(name = "DoB")
    private Date dob;

    @Column(name = "Gender")
    private Character gender;

    @Column(name = "ProfileName")
    private String profileName;

    @Column(name = "Summary")
    private String summary;

    @Column(name = "BanStatus")
    private boolean banStatus;

    // ================ Relationships =============
    @OneToMany(mappedBy = "user")
    private List<Request> requests;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Publisher publisher;

    @ManyToOne
    @JoinColumn(name = "RoleID")
    private Role role;

    @ManyToMany
    @JoinTable(name = "Cart", joinColumns = @JoinColumn(name = "UserID"), inverseJoinColumns = @JoinColumn(name = "GameID"))
    private Set<Game> cartGames;

    @ManyToMany
    @JoinTable(name = "Library", joinColumns = @JoinColumn(name = "UserID"), inverseJoinColumns = @JoinColumn(name = "GameID"))
    private Set<Game> games;

    // @OneToMany(mappedBy = "user")
    // private List<Review> reviews;

    // @OneToMany(mappedBy = "user")
    // private List<Transaction> transaction;
    // ================ Getter & Setter =============

    public User() {
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Double getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(Double walletBalance) {
        this.walletBalance = walletBalance;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public Character getGender() {
        return gender;
    }

    public void setGender(Character gender) {
        this.gender = gender;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Boolean getBanStatus() {
        return banStatus;
    }

    public void setBanStatus(Boolean banStatus) {
        this.banStatus = banStatus;
    }

    public List<Request> getRequests() {
        return requests;
    }

    public void setRequests(List<Request> requests) {
        this.requests = requests;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Set<Game> getCartGames() {
        return cartGames;
    }

    public void setCartGames(Set<Game> cartGames) {
        this.cartGames = cartGames;
    }

    public Set<Game> getGames() {
        return games;
    }

    public void setGames(Set<Game> games) {
        this.games = games;
    }

    // public List<Review> getReviews() {
    // return reviews;
    // }

    // public void setReviews(List<Review> reviews) {
    // this.reviews = reviews;
    // }

}
