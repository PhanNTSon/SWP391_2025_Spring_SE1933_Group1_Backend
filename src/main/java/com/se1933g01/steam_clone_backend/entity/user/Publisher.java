package com.se1933g01.steam_clone_backend.entity.user;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

import com.se1933g01.steam_clone_backend.entity.game.Game;

@Entity
@Table(name = "Publisher")
@Getter
@Setter
public class Publisher {

    @Id
    // PublisherID là khóa ngoại tham chiếu UserID, không phải IDENTITY
    @Column(name = "PublisherID")
    private Long publisherId;

    // Mối quan hệ One-to-One với User
    // PublisherID là PK và cũng là FK tới User.UserID
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Chỉ định rằng PublisherID lấy giá trị từ User.UserID
    @JoinColumn(name = "PublisherID")
    private User user;

    @Column(name = "PublisherName", nullable = false, length = 100)
    private String publisherName;

    @Column(name = "CardNumber", nullable = false, length = 20)
    private String cardNumber; // Chú ý vấn đề bảo mật khi lưu trữ thông tin này!

    // Mối quan hệ One-to-Many với Game
    @OneToMany(mappedBy = "publisher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Game> games;
}