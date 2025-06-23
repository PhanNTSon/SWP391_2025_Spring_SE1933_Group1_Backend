package com.se1933g01.steamclonebackend.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;

@Embeddable
public class CompositedKey implements Serializable {
    private long key1;
    private long key2;
    public CompositedKey() {
    }
    public CompositedKey(long key1, long key2) {
        this.key1 = key1;
        this.key2 = key2;
    }
    public long getKey1() {
        return key1;
    }
    public void setKey1(long key1) {
        this.key1 = key1;
    }
    public long getKey2() {
        return key2;
    }
    public void setKey2(long key2) {
        this.key2 = key2;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != getClass()) return false;
        CompositedKey that = (CompositedKey) obj;
        return key1 == that.key1 && key2 == that.key2;
    }

    @Override
    public int hashCode() {
        return 31 * Long.hashCode(key1) + Long.hashCode(key2);
    }
}
