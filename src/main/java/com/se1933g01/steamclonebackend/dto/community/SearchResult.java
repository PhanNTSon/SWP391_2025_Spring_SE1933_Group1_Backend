package com.se1933g01.steamclonebackend.dto.community;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResult {
    private long userId;
    private String username;
    private String avatarUrl;
}
