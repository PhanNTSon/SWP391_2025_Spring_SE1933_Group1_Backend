package com.se1933g01.steam_clone_backend.entity.transaction;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.se1933g01.steam_clone_backend.entity.CompositedKey;
import com.se1933g01.steam_clone_backend.entity.game.Game;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TransactionDetail")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDetail {
    //================ Attributes =============
    @EmbeddedId
    private CompositedKey id;

    @Column(name = "Price")
    private BigDecimal price;

    //================ Relationships =============
    @ManyToOne
    @MapsId("key1")
    @JoinColumn(name = "TransactionID")
    private Transaction transaction;

    @ManyToOne
    @MapsId("key2")
    @JoinColumn(name = "GameID")
    private Game game;

    //================ Getter & Setter =============
    
}
