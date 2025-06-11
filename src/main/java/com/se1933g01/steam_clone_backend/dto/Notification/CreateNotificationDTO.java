package com.se1933g01.steam_clone_backend.dto.Notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationDTO {
    private long UserId;
    private String notificationType;
    private String notificationContent;
}
