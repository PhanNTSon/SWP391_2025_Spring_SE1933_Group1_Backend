package com.se1933g01.steam_clone_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "SystemRequirement")
@Getter
@Setter
public class SystemRequirement {

    @Id
    // GameID là khóa chính và cũng là khóa ngoại
    @Column(name = "GameID")
    private Integer gameId; // Sẽ được map từ Game

    // Mối quan hệ One-to-One với Game
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // GameID này sẽ lấy giá trị từ ID của Game được liên kết
    @JoinColumn(name = "GameID") // Cột trong SystemRequirement
    private Game game;

    @Column(name = "OS", length = 50)
    private String os;

    @Column(name = "Storage", length = 50)
    private String storage;

    @Column(name = "Processor", length = 50)
    private String processor;

    @Column(name = "Memory", length = 50)
    private String memory;

    @Lob
    @Column(name = "AdditionalNotes", columnDefinition = "TEXT")
    private String additionalNotes;

    @Column(name = "Graphics", length = 50)
    private String graphics;
}