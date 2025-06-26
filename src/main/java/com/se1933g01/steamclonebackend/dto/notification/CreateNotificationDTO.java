package com.se1933g01.steamclonebackend.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationDTO {
    private long receiverId;
    private String receiverName;
    private String notificationType;
    private String notificationContent;
}
