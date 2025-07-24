package com.se1933g01.steamclonebackend.entity.community;

import java.time.LocalDate;

import com.se1933g01.steamclonebackend.entity.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Phan NT Son
 * @since 21-06-2025
 */
@Entity
@Table(name = "Friendships", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Friendship {
    @EmbeddedId
    private FriendshipId friendshipId;

    @ManyToOne
    @MapsId("user1Id")
    @JoinColumn(name = "User1ID")
    private User user1;

    @ManyToOne
    @MapsId("user2Id")
    @JoinColumn(name = "User2ID")
    private User user2;

    @Column(name = "CreatedAt")
    private LocalDate createdAt;

}
