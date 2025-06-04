package com.se1933g01.steam_clone_backend.entity.request;

import java.time.LocalDate;

import com.se1933g01.steam_clone_backend.entity.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Request")
public class Request {
    @Id
    @Column(name = "RequestID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @Column(name = "RequestType")
    private String requestType;

    @Column(name = "TimeCreated")
    private LocalDate timeCreated;

    @Column(name = "UpdatedTime")
    private LocalDate updatedTime;

    @Column(name = "RequestState")
    private int requestState;

    // ================ Relationships =============
    @ManyToOne
    @JoinColumn(name = "UserID")
    private User user;

    @OneToOne(mappedBy = "request")
    private PublisherApplyRequest pAR;

    @OneToOne(mappedBy = "request")
    private AddingGameRequest addingGameRequest;

    @OneToOne(mappedBy = "request")
    private Feedback feedback;

}
