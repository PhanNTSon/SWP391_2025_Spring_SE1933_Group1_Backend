package com.se1933g01.steamclonebackend.entity.community;

import java.time.LocalDate;


import com.se1933g01.steamclonebackend.entity.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "FriendRequests", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RequestID")
    private Long requestId;

    @ManyToOne
    @JoinColumn(name = "SenderID")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "ReceiverID")
    private User receiver;

    @Column(name = "CreatedAt")
    private LocalDate createdAt;
}
