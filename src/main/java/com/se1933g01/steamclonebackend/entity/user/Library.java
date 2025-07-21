package com.se1933g01.steamclonebackend.entity.user; // Hoặc package entity của bạn

import com.se1933g01.steamclonebackend.entity.game.Game;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "Library", schema = "public")
@Getter
@Setter
public class Library {

    // SỬA LỖI Ở ĐÂY: Sử dụng @EmbeddedId để khai báo khóa chính tổ hợp
    @EmbeddedId
    private LibraryId id;

    // Sử dụng @MapsId để liên kết các phần của khóa chính tổ hợp với các Entity
    // tương ứng
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "UserID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("gameId")
    @JoinColumn(name = "GameID")
    private Game game;

    @Column(name = "DateAdded")
    private LocalDateTime dateAdded;

    @Column(name = "PlaytimeInMillis", nullable = false)
    private Long playtimeInMillis = 0L;

    
}
