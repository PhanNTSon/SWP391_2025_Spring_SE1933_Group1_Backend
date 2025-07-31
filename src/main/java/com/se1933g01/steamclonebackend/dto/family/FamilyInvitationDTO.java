package com.se1933g01.steamclonebackend.dto.family;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FamilyInvitationDTO {
    private Long inviteId;
    private Long senderId;
    private Long receiverId;
    private String senderName;
    private String receiverName;
    private String senderAvatar;
    private String receiverAvatar;
    private LocalDate createdAt;
    private LocalDate expiresAt;
}
