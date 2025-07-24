package com.se1933g01.steamclonebackend.entity.user;

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
 * Author: Phan son
 * 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Notifications", schema = "public")
public class Notification {
    @Id
    @Column(name = "NotificationID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long notificationId;

    @Column(name = "NotificationType", length = 100, nullable = false)
    private String notificationType;
    
    @Column(name = "NotificationContent", nullable = false)
    private String notificationContent;
    
    @Column(name = "IsRead")
    private boolean read;

    @ManyToOne
    @JoinColumn(name = "UserID")
    private User user;
}
