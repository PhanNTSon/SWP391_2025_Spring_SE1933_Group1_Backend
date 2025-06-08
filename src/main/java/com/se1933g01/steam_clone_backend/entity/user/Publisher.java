package com.se1933g01.steam_clone_backend.entity.user;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Publisher")
public class Publisher {
    @Id
    @Column(name = "PublisherID")
    private Long publisherId;

    @Column(name = "PublisherName")
    private String publisherName;

    @Column(name = "CardNumber")
    private String cardNumber;
    // ================ Relationships =============
    @OneToOne
    @MapsId
    @JoinColumn(name = "PublisherID")
    private User user;

    // ================ Getter & Setter =============
    public Publisher() {
    }

    public Long getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
