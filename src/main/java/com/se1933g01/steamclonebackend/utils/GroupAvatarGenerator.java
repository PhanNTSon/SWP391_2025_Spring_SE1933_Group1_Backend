package com.se1933g01.steamclonebackend.utils;

import java.util.List;

import org.springframework.stereotype.Component;

import com.se1933g01.steamclonebackend.entity.community.GroupChat;

@Component
public class GroupAvatarGenerator {
    public static List<String> generateGroupAvatar(GroupChat g) {
        List<String> result = g.getMembers().stream()
                .map(member -> member.getMember().getAvatarUrl())
                .toList();
        return result.size() <= 4 ? result : result.subList(0, 3);
    }

    public static List<String> generateGroupAvatar(List<String> memberAvatars) {
        return memberAvatars.size() <= 4 ? memberAvatars : memberAvatars.subList(0, 3);
    }
   
}
