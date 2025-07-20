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
public class GroupChatMemberId implements Serializable {

    @Column(name = "GroupID")
    private Long groupId;
    
    @Column(name = "UserID")
    private Long userId;

}
