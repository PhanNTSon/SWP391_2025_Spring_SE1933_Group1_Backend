package com.se1933g01.steamclonebackend.dto.community;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTO {
    private Long groupId;
    private String groupName;
    private List<String> groupAvatar;
}
