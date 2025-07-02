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
@Table(name = "Publisher", schema = "public")
public class Publisher {
    @Id
    @Column(name = "PublisherID")
    private Long publisherId;

    @Column(name = "PublisherName", length = 100, nullable = false)
    private String publisherName;

    @Column(name = "CardNumber", length = 20)
    private String cardNumber;

    @Column(name = "LegalName", length = 255)
    private String legalName;

    @Column(name = "Address", length = 255)
    private String address;

    @Column(name = "SocialNumber", length = 12)
    private String socialNumber;

    @Column(name = "Country", length = 255)
    private String country;

    @Column(name = "ImageUrl", length = 255)
    private String imageUrl;
    
    @Column(name = "Suspend")
    private Integer suspend;
    // ================ Relationships =============
    @OneToOne
    @MapsId
    @JoinColumn(name = "PublisherID")
    private User user;

    // ================ Getter & Setter =============

}
