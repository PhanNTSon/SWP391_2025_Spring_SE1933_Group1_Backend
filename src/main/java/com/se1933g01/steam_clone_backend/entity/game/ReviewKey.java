package com.se1933g01.steam_clone_backend.entity.game;

import java.io.Serializable;


import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Author: Phan Son
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ReviewKey implements Serializable{
    private long gameId;
    private long userId;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != getClass()) return false;
        ReviewKey that = (ReviewKey) obj;
        return gameId == that.getGameId() && userId == that.getUserId();
    }

    @Override
    public int hashCode() {
        return 31 * Long.hashCode(gameId) + Long.hashCode(userId);
    }
}
