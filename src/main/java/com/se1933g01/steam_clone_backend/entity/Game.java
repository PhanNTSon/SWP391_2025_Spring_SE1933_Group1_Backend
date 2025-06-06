// package com.se1933g01.steam_clone_backend.entity;

// import jakarta.persistence.*;
// import lombok.Getter;
// import lombok.Setter;

// import java.math.BigDecimal;
// import java.util.Date;
// import java.util.HashSet;
// import java.util.Set;

// import com.se1933g01.steam_clone_backend.entity.game.Tag;
// import com.se1933g01.steam_clone_backend.entity.user.Publisher;

// @Entity
// @Table(name = "Game")
// @Getter
// @Setter
// public class Game {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     @Column(name = "GameID")
//     private Integer gameId;

//     // Mối quan hệ Many-to-One với Publisher
//     @ManyToOne(fetch = FetchType.LAZY) // LAZY là tốt cho hiệu năng
//     @JoinColumn(name = "PublisherID")
//     private Publisher publisher;

//     @Column(name = "[Name]", nullable = false, length = 100)
//     private String name;

//     @Temporal(TemporalType.DATE)
//     @Column(name = "ReleaseDate")
//     private Date releaseDate;

//     @Column(name = "[State]")
//     private Boolean state; // Ví dụ: true = Released, false = Unreleased

//     @Column(name = "Price", precision = 10, scale = 2)
//     private BigDecimal price;

//     @Column(name = "ShortDescription", length = 255)
//     private String shortDescription;

//     @Lob
//     @Column(name = "FullDescription", columnDefinition = "TEXT")
//     private String fullDescription;

//     @Column(name = "TotalPurchased")
//     private Integer totalPurchased;

//     @Column(name = "OS", length = 50)
//     private String os;

//     @Column(name = "Storage", length = 50)
//     private String storage;

//     @Column(name = "Processor", length = 50)
//     private String processor;

//     @Column(name = "Memory", length = 50)
//     private String memory;

//     @Lob
//     @Column(name = "AdditionalNotes", columnDefinition = "TEXT")
//     private String additionalNotes;

//     @Column(name = "Graphics", length = 50)
//     private String graphics;

//     // Mối quan hệ Many-to-Many với Tags thông qua bảng GameTags
//     @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
//     @JoinTable(name = "GameTags", // Tên bảng trung gian
//             joinColumns = @JoinColumn(name = "GameID"), // Khóa ngoại trỏ về Game
//             inverseJoinColumns = @JoinColumn(name = "TagID") // Khóa ngoại trỏ về Tag
//     )
//     private Set<Tag> tags = new HashSet<>();

//     // Mối quan hệ One-to-Many với Media
//     @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//     private Set<Media> media = new HashSet<>();

//     // ... các mối quan hệ khác như Library, Review, TransactionDetail, Cart

//     public void addTag(Tag tag) {
//         this.tags.add(tag);
//         // tag.getGames().add(this); // Nếu có two-way relationship
//     }

//     public void removeTag(Tag tag) {
//         this.tags.remove(tag);
//         // tag.getGames().remove(this); // Nếu có two-way relationship
//     }
// }