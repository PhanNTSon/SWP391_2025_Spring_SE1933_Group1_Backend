package com.se1933g01.steamclonebackend.entity.news;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.se1933g01.steamclonebackend.entity.game.Game;
import com.se1933g01.steamclonebackend.entity.game.Media;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "News")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class News {
    @Column(name = "NewsID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long newsId;

    @Column(name = "Title", nullable = false)
    private String title;

    @Column(name = "Summary", nullable = false)
    private String summary;

    @Column(name = "Markdown")
    private String markdown;  // Markdown format

    @Column(name = "CreatedAt")
    private LocalDate createdAt;

    @Column(name="Thumbnail")
    private String thumbnail;

    @ManyToOne
    @JoinColumn(name = "GameID")
    private Game game;

    // @OneToMany(mappedBy = "news", cascade = CascadeType.ALL)
    // private List<Media> media = new ArrayList<>();
}
