package com.se1933g01.steamclonebackend.entity.community;

import java.time.LocalDateTime;

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

@Entity
@Table(name = "GroupChatMember", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupChatMember {

    @EmbeddedId
    private GroupChatMemberId id;

    @ManyToOne
    @MapsId("groupId")
    @JoinColumn(name = "GroupID")
    private GroupChat group;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "UserID")
    private User member;

    @Column(name = "IsAdmin")
    private boolean isAdmin;

    @Column(name = "JoinedAt")
    private LocalDateTime joinedAt;
}
