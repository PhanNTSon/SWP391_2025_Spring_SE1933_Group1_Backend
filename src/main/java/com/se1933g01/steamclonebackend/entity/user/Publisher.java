package com.se1933g01.steamclonebackend.entity.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    @Column(name = "LegalName")
    private String legalName;

    @Column(name = "Address")
    private String address;

    @Column(name = "SocialNumber")
    private String socialNumber;

    @Column(name = "Country")
    private String country;

    @Column(name = "ImageUrl")
    private String imageUrl;
    // ================ Relationships =============
    @OneToOne
    @MapsId
    @JoinColumn(name = "PublisherID")
    private User user;

    // ================ Getter & Setter =============

}
