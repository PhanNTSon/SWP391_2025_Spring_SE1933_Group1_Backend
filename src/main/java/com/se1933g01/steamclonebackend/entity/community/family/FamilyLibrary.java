package com.se1933g01.steamclonebackend.entity.community.family;

import com.se1933g01.steamclonebackend.entity.game.Game;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FamilyLibrary", schema = "public")
public class FamilyLibrary {
    
    @EmbeddedId
    private FamilyLibraryId id;

    @MapsId("familyID")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FamilyID")
    private Family family;

    @MapsId("gameID")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GameID")
    private Game game;

}
