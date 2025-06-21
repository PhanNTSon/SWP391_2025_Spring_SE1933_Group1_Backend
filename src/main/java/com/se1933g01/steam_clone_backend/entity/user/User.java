package com.se1933g01.steam_clone_backend.entity.user;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.se1933g01.steam_clone_backend.entity.community.Conversation;
import com.se1933g01.steam_clone_backend.entity.community.Friendship;
import com.se1933g01.steam_clone_backend.entity.community.Message;
import com.se1933g01.steam_clone_backend.entity.game.Game;
import com.se1933g01.steam_clone_backend.entity.game.Review;
import com.se1933g01.steam_clone_backend.entity.request.Request;

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
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Phan Son
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "User")
public class User {

        @Id
        @Column(name = "UserID")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long userId;

        @Column(name = "Email")
        private String email;

        @Column(name = "Username")
        private String username;

        @Column(name = "Password")
        private String password;

        @Column(name = "WalletBalance")
        private BigDecimal walletBalance;

        @Column(name = "AvatarUrl")
        private String avatarUrl;

        @Column(name = "Country")
        private String country;

        @Column(name = "DoB")
        private Date dob;

        @Column(name = "Gender")
        private Character gender;

        @Column(name = "ProfileName")
        private String profileName;

        @Column(name = "Summary")
        private String summary;

        @Column(name = "BanStatus")
        private boolean banStatus;

        // ================ Relationships =============
        @OneToMany(mappedBy = "user")
        private List<Request> requests;

        @OneToMany(mappedBy = "user")
        private List<Notification> notifications;

        @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
        private Publisher publisher;

        @ManyToOne
        @JoinColumn(name = "RoleID")
        private Role role;

        @ManyToMany
        @JoinTable(name = "Cart", joinColumns = @JoinColumn(name = "UserID"), inverseJoinColumns = @JoinColumn(name = "GameID"))
        private Set<Game> cartGames = new HashSet<>();

        @ManyToMany
        @JoinTable(name = "Library", joinColumns = @JoinColumn(name = "UserID"), inverseJoinColumns = @JoinColumn(name = "GameID"))
        private Set<Game> games;

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

        /**
         * @since 21-06-2025
         */
        @OneToMany(mappedBy = "user")
        private List<Friendship> friendsInit;

        @OneToMany(mappedBy = "friend")
        private List<Friendship> friendsRecieve;

        @OneToMany(mappedBy = "user1")
        private List<Conversation> conversationInit;
        
        @OneToMany(mappedBy = "user2")
        private List<Conversation> conversationReceive;
        
        @OneToMany(mappedBy = "sender")
        @JsonIgnore
        private List<Message> messages;
        // --!!

}
