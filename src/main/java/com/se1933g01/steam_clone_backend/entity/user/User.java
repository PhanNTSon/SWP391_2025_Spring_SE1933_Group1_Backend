package com.se1933g01.steam_clone_backend.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set; // Sẽ dùng cho UserRoles

import com.se1933g01.steam_clone_backend.entity.game.Game;
import com.se1933g01.steam_clone_backend.entity.game.Review;
import com.se1933g01.steam_clone_backend.entity.request.Request;
import com.se1933g01.steam_clone_backend.entity.transaction.Transaction;

@Entity
@Table(name = "[User]") // Sử dụng dấu ngoặc vuông vì "User" có thể là từ khóa
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    private Long userId;

    @Column(name = "Email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "Username", nullable = false, length = 50)
    private String username;

    @Column(name = "[Password]", nullable = false, length = 255) // Dùng dấu ngoặc vuông
    private String password; // NHỚ: Luôn lưu trữ mật khẩu đã được hash!

    @Column(name = "WalletBalance", precision = 10, scale = 2)
    private Double walletBalance;

    @Column(name = "Country", length = 50)
    private String country;

    @Temporal(TemporalType.DATE) // Quan trọng cho kiểu DATE của SQL
    @Column(name = "DoB")
    private Date dob;

    @Column(name = "Gender", length = 1)
    private Character gender; // Hoặc String

    @Column(name = "ProfileName", length = 50)
    private String profileName;

    @Lob // Dùng cho kiểu TEXT của SQL
    @Column(name = "Summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "BanStatus")
    private Boolean banStatus;

    // Mối quan hệ (sẽ thêm sau, ví dụ UserRoles)
    // @OneToMany(mappedBy = "user")
    // private Set<UserRole> userRoles;
 //================ Relationships =============
    @OneToMany(mappedBy = "user")
    private List<Request> requests;
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Publisher publisher;
    
    @ManyToOne
    @JoinColumn(name = "RoleID")
    private Role role;

    @ManyToMany
    @JoinTable(
        name = "Cart",
        joinColumns = @JoinColumn(name = "UserID"),
        inverseJoinColumns = @JoinColumn(name = "GameID")
    )
    private Set<Game> cartGames;

    @ManyToMany
    @JoinTable(
        name = "Library",
        joinColumns = @JoinColumn(name = "UserID"),
        inverseJoinColumns = @JoinColumn(name = "GameID")
    )
    private Set<Game> games;

    @OneToMany(mappedBy = "user")
    private List<Review> reviews;

    @OneToMany(mappedBy = "user")
    private List<Transaction> transaction;
    // Constructors, Getters, Setters (Lombok)
}