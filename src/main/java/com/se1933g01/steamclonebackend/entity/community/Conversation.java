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

/**
 * @author Phan NT Son
 * @since 21-06-2025
 */
@Entity
@Table(name = "Conversations", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ConversationID")
    private long conversationId;

    /*
     * Make sure UserID1 is always smaller than UserID2
     * due to the constrains in DB → for no duplicate
     */
    @ManyToOne
    @JoinColumn(name = "UserID1", nullable = false)
    private User user1;

    @ManyToOne
    @JoinColumn(name = "UserID2", nullable = false)
    private User user2;

    @Column(name = "CreatedAt")
    private LocalDate createdAt;

}
