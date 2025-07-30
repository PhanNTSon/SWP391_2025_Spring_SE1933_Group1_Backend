package com.se1933g01.steamclonebackend.dto.family;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FamilyInfoDTO {
    private Long familyId;
    private Long ownerId;
    private Boolean isOwner;
    private List<FamilyMemberDTO> members;
    private List<FamilyGameDTO> games;
    private LocalDate expDate;
}
