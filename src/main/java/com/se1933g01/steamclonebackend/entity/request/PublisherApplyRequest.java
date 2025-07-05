package com.se1933g01.steamclonebackend.entity.request;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
@Data
@Entity
@Table(name = "PublisherApplyRequest", schema = "public")
public class PublisherApplyRequest {
    @Id
    @Column(name = "RequestID")
    private Long requestId;

    @Column(name = "PublisherName", length = 100)
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
    

    // ================ Relationships =============
    @OneToOne
    @MapsId
    @JoinColumn(name = "RequestID")
    private Request request;
}
