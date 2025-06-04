package com.se1933g01.steam_clone_backend.entity.game;

import java.io.Serializable;

import jakarta.persistence.Embeddable;

@Embeddable
public class ReviewKey implements Serializable {
    private long gameId;
    private long userId;
    public ReviewKey() {
    }
    public ReviewKey(long gameId, long userId) {
        this.gameId = gameId;
        this.userId = userId;
    }
    public long getKey1() {
        return gameId;
    }
    public void setKey1(long gameId) {
        this.gameId = gameId;
    }
    public long getKey2() {
        return userId;
    }
    public void setKey2(long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != getClass()) return false;
        ReviewKey that = (ReviewKey) obj;
        return gameId == that.gameId && userId == that.userId;
    }

    @Override
    public int hashCode() {
        return 31 * Long.hashCode(gameId) + Long.hashCode(userId);
    }
}
