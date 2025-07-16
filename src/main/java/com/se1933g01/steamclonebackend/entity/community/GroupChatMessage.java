package com.se1933g01.steamclonebackend.entity.community;

import java.time.LocalDateTime;

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
@Table(name = "GroupChatMessage", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MessageID")
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GroupID", nullable = false)
    private Long groupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SenderID", nullable = false)
    private User sender;

    @Column(name = "Message", nullable = false)
    private String message;

    @Column(name = "SentAt")
    private LocalDateTime sentAt;
}
