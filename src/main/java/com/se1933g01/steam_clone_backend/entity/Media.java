package com.se1933g01.steam_clone_backend.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "Media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MediaID")
    private Integer mediaId;

    // Mối quan hệ Many-to-One với Game
    // Một Game có thể có nhiều Media
    // Một Media thuộc về một Game
    @ManyToOne(fetch = FetchType.LAZY) // LAZY là tốt cho hiệu năng
    @JoinColumn(name = "GameID", nullable = false) // Cột khóa ngoại trong bảng Media
                                                   // nullable=false vì thường media phải thuộc về một game
    private Game game;

    @Column(name = "[Url]", nullable = false, length = 255) // Sử dụng dấu ngoặc vuông nếu "Url" là từ khóa
    private String url; // Tên thuộc tính Java có thể là "url" hoặc "mediaUrl"

    @Column(name = "[Type]", nullable = false, length = 20) // Sử dụng dấu ngoặc vuông nếu "Type" là từ khóa
    private String type; // Tên thuộc tính Java có thể là "type" hoặc "mediaType"
                         // ví dụ: 'image', 'video_thumbnail', 'video_trailer', 'screenshot'

    // toString(), equals(), hashCode() có thể được thêm bởi Lombok (@ToString,
    // @EqualsAndHashCode)
    // hoặc tự viết nếu cần.
}