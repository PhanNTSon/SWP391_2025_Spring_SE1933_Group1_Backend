package com.se1933g01.steamclonebackend.entity.community.family;

import java.time.LocalDate;

import com.se1933g01.steamclonebackend.entity.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "FamilyInvitation", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FamilyInvitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "InviteID")
    private Long inviteID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FamilyID", nullable = false)
    private Family family;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "InvitorID", nullable = false)
    private User invitor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ReceiverID", nullable = false)
    private User receiver;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDate createdAt;

    @Column(name = "ExpiresAt", nullable = false)
    private LocalDate expiresAt;
}
