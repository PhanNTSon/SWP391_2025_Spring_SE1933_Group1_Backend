package com.se1933g01.steamclonebackend.entity.community.family;

import java.time.LocalDate;

import com.se1933g01.steamclonebackend.entity.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "FamilyMember", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FamilyMember {
    @EmbeddedId
    private FamilyMemberId id;

    @MapsId("familyID")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FamilyID")
    private Family family;

    @MapsId("userID")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID")
    private User user;

    @Column(name = "IsOwner",nullable = false)
    private boolean isOwner;

    @Column(name = "JoinedAt",nullable = false, updatable = false)
    private LocalDate joinedAt;
}
