package com.se1933g01.steamclonebackend.entity.community.family;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FamilyMemberId implements Serializable {
    private Long familyID;
    private Long userID;
}
