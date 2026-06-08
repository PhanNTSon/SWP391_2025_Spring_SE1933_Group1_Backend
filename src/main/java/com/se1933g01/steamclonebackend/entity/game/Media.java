package com.se1933g01.steamclonebackend.entity.game;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Media", schema = "public")
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MediaID")
    private long mediaId;

    @Column(name = "Url")
    private String url;

    @Column(name = "Type", length = 20)
    private String type;

    // ================ Relationships =============
    @ManyToOne
    @JoinColumn(name = "GameID")
    private Game game;
    
    // @ManyToOne
    // @JoinColumn(name = "NewsID")
    // private News news;

}
