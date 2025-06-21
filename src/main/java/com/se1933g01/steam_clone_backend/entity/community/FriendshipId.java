package com.se1933g01.steam_clone_backend.entity.community;

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
    private Long userId;
    private long friendId;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || obj.getClass() != getClass())
            return false;
        FriendshipId that = (FriendshipId) obj;
        return userId == that.userId && friendId == that.friendId;
    }

    @Override
    public int hashCode() {
        return 31 * Long.hashCode(userId) + Long.hashCode(friendId);
    }
}
