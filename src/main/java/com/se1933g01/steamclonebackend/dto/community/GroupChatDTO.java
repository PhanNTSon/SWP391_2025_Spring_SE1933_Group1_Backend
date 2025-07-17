package com.se1933g01.steamclonebackend.dto.community;

import java.util.List;

import com.se1933g01.steamclonebackend.dto.user.FriendDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupChatDTO {
    private List<FriendDTO> members;
    private List<MessageDTO> messages;
}
