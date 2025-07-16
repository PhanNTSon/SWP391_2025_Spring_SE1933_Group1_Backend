package com.se1933g01.steamclonebackend.entity.user;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.se1933g01.steamclonebackend.entity.community.Block;
import com.se1933g01.steamclonebackend.entity.community.Conversation;
import com.se1933g01.steamclonebackend.entity.community.FriendRequest;
import com.se1933g01.steamclonebackend.entity.community.Friendship;
import com.se1933g01.steamclonebackend.entity.community.Message;
import com.se1933g01.steamclonebackend.entity.game.Game;
import com.se1933g01.steamclonebackend.entity.game.Review;
import com.se1933g01.steamclonebackend.entity.request.Request;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @Author: Phan Son
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "User", schema = "public")
public class User {

        @Id
        @Column(name = "UserID")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long userId;

        @Column(name = "Email", length = 100, unique = true, nullable = false)
        private String email;

        @Column(name = "Username", length = 50, unique = true, nullable = false)
        private String username;

        @Column(name = "Password", length = 255, nullable = false)
        private String password;

        @Column(name = "WalletBalance")
        private BigDecimal walletBalance;

        @Column(name = "AvatarURL")
        private String avatarUrl;

        @Column(name = "Country", length = 50)
        private String country;

        @Column(name = "DoB")
        private LocalDate dob;

        @Column(name = "Gender")
        private Character gender;

        @Column(name = "ProfileName", length = 50)
        private String profileName;

        @Column(name = "Summary")
        private String summary;

        @Column(name = "BanStatus")
        private boolean banStatus = false;

        @Column(name = "CreatedAt")
        private LocalDate createdAt;

        @Column(name = "ThemeName", length = 50)
        private String themeName;

        @Column(name = "NewEmailAddress", length = 100)
        private String newEmailAddress;

        @Column(name = "EmailChangeToken", length = 10)
        private String emailChangeToken;

        @Column(name = "EmailChangeTokenExpiry")
        private LocalDateTime emailChangeTokenExpiry;

        // ================ Relationships =============
        @OneToMany(mappedBy = "user")
        private List<Request> requests;

        @OneToMany(mappedBy = "user")
        private List<Notification> notifications;

        @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
        private Publisher publisher;

        @ManyToOne
        @JoinColumn(name = "RoleID", nullable = false)
        private Role role;

        @ManyToMany
        @JoinTable(name = "Cart", joinColumns = @JoinColumn(name = "UserID"), inverseJoinColumns = @JoinColumn(name = "GameID"))
        private Set<Game> cartGames = new HashSet<>();

        @OneToMany(mappedBy = "user")
        private Set<Library> libraryGames = new HashSet<>();

        @ManyToMany
        @JoinTable(name = "ReviewHelpful", joinColumns = @JoinColumn(name = "HelpfulUserID"), inverseJoinColumns = {
                        @JoinColumn(name = "ReviewGameID", referencedColumnName = "GameID"),
                        @JoinColumn(name = "ReviewUserID", referencedColumnName = "UserID")
        })
        @JsonIgnore
        private Set<Review> likedReviews;

        @ManyToMany
        @JoinTable(name = "ReviewNotHelpful", joinColumns = @JoinColumn(name = "NotHelpfulUserID"), inverseJoinColumns = {
                        @JoinColumn(name = "ReviewGameID", referencedColumnName = "GameID"),
                        @JoinColumn(name = "ReviewUserID", referencedColumnName = "UserID")
        })
        @JsonIgnore
        private Set<Review> unlikedReviews;

        // I'm the one who send invite
        @OneToMany(mappedBy = "sender")
        private List<FriendRequest> friendsInit;

        // I'm being sent by someone
        @OneToMany(mappedBy = "receiver")
        private List<FriendRequest> friendsRecieve;

        @OneToMany(mappedBy = "user1")
        private List<Conversation> conversationInit;

        @OneToMany(mappedBy = "user2")
        private List<Conversation> conversationReceive;

        @OneToMany(mappedBy = "sender")
        @JsonIgnore
        private List<Message> messages;

        // I'm the one who block
        @OneToMany(mappedBy = "blocker", cascade = CascadeType.ALL)
        private List<Block> blocksSent;

        // I'm being blocked by someone
        @OneToMany(mappedBy = "blocked", cascade = CascadeType.ALL)
        private List<Block> blocksReceived;

        @OneToMany(mappedBy = "user1")
        @JsonIgnore
        private List<Friendship> friendList;

        @OneToMany(mappedBy = "user2")
        @JsonIgnore
        private List<Friendship> isFriendList;

}
