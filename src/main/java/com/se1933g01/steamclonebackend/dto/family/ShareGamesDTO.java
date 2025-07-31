package com.se1933g01.steamclonebackend.dto.family;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShareGamesDTO {
    private List<Long> gameIds;    
}
