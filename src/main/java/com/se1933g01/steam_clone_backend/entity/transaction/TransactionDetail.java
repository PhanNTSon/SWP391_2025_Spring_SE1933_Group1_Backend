package com.se1933g01.steam_clone_backend.entity.transaction;

import com.se1933g01.steam_clone_backend.entity.CompositedKey;
import com.se1933g01.steam_clone_backend.entity.game.Game;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "TransactionDetail")
public class TransactionDetail {
    //================ Attributes =============
    @EmbeddedId
    private CompositedKey id;

    @Column(name = "Price")
    private double price;

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
