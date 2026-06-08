package com.se1933g01.steamclonebackend.dto.family;


import com.se1933g01.steamclonebackend.dto.user.LibraryGameDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class FamilyGameDTO extends LibraryGameDTO{
    private Boolean isPlayable;

    public FamilyGameDTO(LibraryGameDTO libraryGameDTO, Boolean isPlayable) {
        this.setGameDetail(libraryGameDTO.getGameDetail());
        this.setDateAdded(libraryGameDTO.getDateAdded());
        this.setPlaytimeInMillis(libraryGameDTO.getPlaytimeInMillis());
        this.setLastTimePlayed(libraryGameDTO.getLastTimePlayed());
        this.isPlayable = isPlayable;
    }
}
