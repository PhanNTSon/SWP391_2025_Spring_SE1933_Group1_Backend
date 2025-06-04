package com.se1933g01.steam_clone_backend.entity.request;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "PublisherApplyRequest")
public class PublisherApplyRequest {
    @Id
    @Column(name = "RequestID")
    private Long requestId;

    @Column(name = "PublisherName")
    private String publisherName;

    @Column(name = "CardNumber")
    private String cardNumber;

    // ================ Relationships =============
    @OneToOne
    @MapsId
    @JoinColumn(name = "RequestID")
    private Request request;
}
