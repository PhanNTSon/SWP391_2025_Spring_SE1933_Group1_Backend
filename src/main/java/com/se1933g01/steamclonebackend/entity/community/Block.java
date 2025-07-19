package com.se1933g01.steamclonebackend.entity.community;

import java.time.LocalDate;

import com.se1933g01.steamclonebackend.entity.user.User;

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
@Table(name = "Blocks", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Block {

    @EmbeddedId
    private BlockId blockId;

    @ManyToOne
    @JoinColumn(name = "BlockerID")
    @MapsId("blockerId")
    private User blocker;

    @ManyToOne
    @JoinColumn(name = "BlockedID")
    @MapsId("blockedId")
    private User blocked;

    @Column(name = "CreatedAt")
    private LocalDate createdAt;
}
