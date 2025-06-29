package com.se1933g01.steamclonebackend.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private long notifId;
    private String notificationType;
    private String notificationContent;
    private boolean read;
}
