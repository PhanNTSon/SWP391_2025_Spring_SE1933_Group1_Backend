package com.se1933g01.steamclonebackend.entity.community.family;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FamilyLibraryId implements Serializable {
    private Long familyID;
    private Long gameID;
}
