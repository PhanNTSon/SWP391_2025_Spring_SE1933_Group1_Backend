package com.se1933g01.steam_clone_backend.entity.request;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
@Data
@Entity
@Table(name = "Feedback")
public class Feedback {
    @Id
    @Column(name = "RequestID")
    private long requestId;

    @Column(name = "Subject",columnDefinition = "NVARCHAR(256)")
    private String subject;
    @Column(name = "Message",columnDefinition = "NVARCHAR(2048)")
    private String message;

    // ================ Relationships =============
    @OneToOne
    @MapsId
    @JoinColumn(name = "RequestID")
    private Request request;
}
