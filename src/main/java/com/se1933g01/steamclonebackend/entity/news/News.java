package com.se1933g01.steamclonebackend.entity.news;

import java.time.LocalDate;

import com.se1933g01.steamclonebackend.entity.game.Game;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @Column(name = "Title", nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(name = "Summary", nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(name = "Markdown", columnDefinition = "TEXT")
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
