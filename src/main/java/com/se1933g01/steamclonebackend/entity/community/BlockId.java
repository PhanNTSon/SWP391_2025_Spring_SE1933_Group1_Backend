package com.se1933g01.steamclonebackend.entity.community;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockId implements Serializable {

    @Column(name = "BlockerID")
    private Long blockerId;
    
    @Column(name = "BlockedID")
    private Long blockedId;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || obj.getClass() != getClass())
            return false;
        BlockId that = (BlockId) obj;
        return blockerId == that.blockerId && blockedId == that.blockedId;
    }

    @Override
    public int hashCode() {
        return 31 * Long.hashCode(blockerId) + Long.hashCode(blockedId);
    }

}
