package com.se1933g01.steamclonebackend.entity.transaction;

import java.math.BigDecimal;

import com.se1933g01.steamclonebackend.entity.CompositedKey;
import com.se1933g01.steamclonebackend.entity.game.Game;

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
@Table(name = "TransactionDetail", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDetail {
    //================ Attributes =============
    @EmbeddedId
    private CompositedKey id;

    @Column(name = "Price", precision = 10, scale = 2)
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
