package com.se1933g01.steamclonebackend.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.se1933g01.steamclonebackend.dto.ApiRespDTO;
import com.se1933g01.steamclonebackend.dto.family.FamilyInfoDTO;
import com.se1933g01.steamclonebackend.dto.family.ShareGamesDTO;
import com.se1933g01.steamclonebackend.dto.family.SubscriptionPlanDTO;
import com.se1933g01.steamclonebackend.entity.user.CustomUserDetail;
import com.se1933g01.steamclonebackend.service.FamilyService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/family")
public class FamilyController {
    private final FamilyService familyService;

    public FamilyController(FamilyService familyService) {
        this.familyService = familyService;
    }

    @GetMapping("")
    public ResponseEntity<ApiRespDTO<?>> getFamily(@AuthenticationPrincipal CustomUserDetail me) {
        return ResponseEntity.ok().body(new ApiRespDTO<FamilyInfoDTO>(true, "GET_SUCCESS", "Get Success",
                familyService.getFamily(me.getUser().getUserId())));
    }

    @PostMapping("/subscribe")
    public ResponseEntity<ApiRespDTO<?>> subscribePlan(@RequestBody SubscriptionPlanDTO plan,
            @AuthenticationPrincipal CustomUserDetail me) {
        return ResponseEntity.ok()
                .body(new ApiRespDTO<FamilyInfoDTO>(true, "SUBSCRIBE_SUCCESS", "Subscription successful",
                        familyService.subscribePlan(plan, me.getUser().getUserId())));
    }

    @PostMapping("/library/share")
    public ResponseEntity<ApiRespDTO<?>> shareGames(@RequestBody ShareGamesDTO dto, @AuthenticationPrincipal CustomUserDetail me) {
        
        return ResponseEntity.ok()
                .body(new ApiRespDTO<FamilyInfoDTO>(true, "SHARE_GAMES_SUCCESS", "Games shared successfully",
                        familyService.shareGames(dto, me.getUser().getUserId())));
    }

    @PostMapping("/library/remove")
    public ResponseEntity<ApiRespDTO<?>> removeGameFromLibrary(@RequestBody ShareGamesDTO dto,
            @AuthenticationPrincipal CustomUserDetail me) {
        return ResponseEntity.ok()
                .body(new ApiRespDTO<FamilyInfoDTO>(true, "REMOVE_GAME_SUCCESS", "Game removed successfully",
                        familyService.removeGameFromLibrary(dto, me.getUser().getUserId())));
    }
    
}
