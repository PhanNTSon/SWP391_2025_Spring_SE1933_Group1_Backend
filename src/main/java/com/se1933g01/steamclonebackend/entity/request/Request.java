package com.se1933g01.steamclonebackend.entity.request;

import java.time.LocalDate;

import com.se1933g01.steamclonebackend.entity.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
@Table(name = "Request", schema = "public")
public class Request {
    @Id
    @Column(name = "RequestID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @Column(name = "RequestType", length = 50)
    private String requestType;

    @Column(name = "TimeCreated")
    private LocalDate timeCreated;

    @Column(name = "UpdatedTime")
    private LocalDate updatedTime;

    @Column(name = "Status")
    private Integer requestState;
    
    // ================ Relationships =============
    @ManyToOne
    @JoinColumn(name = "UserID")
    private User user;


    @OneToOne(mappedBy = "request",cascade = jakarta.persistence.CascadeType.ALL)
    private AddingGameRequest addingGameRequest;


}
