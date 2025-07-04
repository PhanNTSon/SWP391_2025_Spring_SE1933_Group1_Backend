package com.se1933g01.steamclonebackend.entity.community;

import java.time.LocalDateTime;

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
@Table(name = "Messages", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MessageID")
    private long messageId;

    @ManyToOne
    @JoinColumn(name = "ConversationID")
    private Conversation conversation;

    @ManyToOne
    @JoinColumn(name = "SenderID", nullable = false)
    private User sender;

    @Column(name = "MessageContent")
    private String messageContent;

    @Column(name = "SentAt")
    private LocalDateTime sentAt;

    @Column(name = "IsRead")
    private boolean messageRead;
}
