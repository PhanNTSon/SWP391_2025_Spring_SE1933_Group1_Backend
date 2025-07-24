package com.se1933g01.steamclonebackend.entity.community;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipId implements Serializable {
    private Long user1Id;

    private Long user2Id;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || obj.getClass() != getClass())
            return false;
        FriendshipId that = (FriendshipId) obj;
        return user1Id == that.user1Id && user2Id == that.user2Id;
    }

    @Override
    public int hashCode() {
        return 31 * Long.hashCode(user1Id) + Long.hashCode(user2Id);
    }
}
