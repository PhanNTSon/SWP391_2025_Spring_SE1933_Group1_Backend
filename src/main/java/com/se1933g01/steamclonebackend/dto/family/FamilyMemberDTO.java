package com.se1933g01.steamclonebackend.dto.family;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FamilyMemberDTO {
    private Long id;
    private String name;
    private String avatar;
    private Boolean isOwner;
}
