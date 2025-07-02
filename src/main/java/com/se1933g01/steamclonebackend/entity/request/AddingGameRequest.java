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
@Table(name = "AddingGameRequest")
public class AddingGameRequest {
    @Id
    @Column(name = "RequestID")
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long requestId;

    @Column(name = "GameName")
    private String gameName;
    @Column(name = "ReleaseDate")
    private LocalDate releaseDate;
    @Column(name = "Price")
    private BigDecimal price;
    @Column(name = "ShortDescription", columnDefinition = "NVARCHAR(MAX)")
    private String shortDescription;
    @Column(name = "FullDescription", columnDefinition = "NVARCHAR(MAX)")
    private String fullDescription;
    @Column(name = "Os")
    private String os;
    @Column(name = "Storage")
    private String storage;
    @Column(name = "Processor")
    private String processor;
    @Column(name = "Memory")
    private String memory;
    @Column(name = "AdditionalNotes")
    private String additionalNotes;
    @Column(name = "Graphics")
    private String graphics;
    @Column(name = "GameUrl")
    private String gameUrl;
    @Column(name = "IconUrl")
    private String iconUrl;

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
