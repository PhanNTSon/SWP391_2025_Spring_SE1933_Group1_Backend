package com.se1933g01.steamclonebackend.entity.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: Phan Son
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "AddingGameRequest", schema = "public")
public class AddingGameRequest {
    @Id
    @Column(name = "RequestID")
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long requestId;

    @Column(name = "GameId")
    private Long gameId;

    @Column(name = "GameName", length = 100)
    private String gameName;

    @Column(name = "ReleaseDate")
    private LocalDate releaseDate;

    @Column(name = "Price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "ShortDescription")
    private String shortDescription;

    @Column(name = "FullDescription")
    private String fullDescription;

    @Column(name = "OS", length = 50)
    private String os;

    @Column(name = "Storage", length = 50)
    private String storage;

    @Column(name = "Processor", length = 50)
    private String processor;

    @Column(name = "Memory", length = 50)
    private String memory;

    @Column(name = "AdditionalNotes")
    private String additionalNotes;
    
    @Column(name = "Graphics", length = 50)
    private String graphics;
    
    @Column(name = "GameUrl", length = 255)
    private String gameUrl;

    @Column(name = "IconUrl", length = 255)
    private String iconUrl;

    @Column(name = "DeclineMessage", length = 255)
    private String declineMessage;

    @Column(name = "UpdateLog")
    private String updateLog;

    @Column(name = "MediaUrls")
    @ElementCollection
    @CollectionTable(name = "AddingGameRequest_mediaUrls", joinColumns = @JoinColumn(name = "RequestID"))
    private List<String> mediaUrls;

    @Column(name = "Tags")
    @ElementCollection
    @CollectionTable(name = "AddingGameRequest_tags", joinColumns = @JoinColumn(name = "RequestID"))
    private List<Integer> tags;

    // ================ Relationships =============

    @OneToOne
    @MapsId
    @JoinColumn(name = "RequestID")
    private Request request;

    // ================ Getter & Setter =============

}
