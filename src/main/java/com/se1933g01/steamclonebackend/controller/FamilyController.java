package com.se1933g01.steamclonebackend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.se1933g01.steamclonebackend.dto.ApiRespDTO;
import com.se1933g01.steamclonebackend.dto.family.FamilyInfoDTO;
import com.se1933g01.steamclonebackend.dto.family.FamilyInvitationDTO;
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

        /**
         * Get family information for the authenticated user including family members,
         * games.
         * 
         * @param me
         * @return
         */
        @GetMapping("")
        public ResponseEntity<ApiRespDTO<?>> getFamilyInfo(@AuthenticationPrincipal CustomUserDetail me) {
                return ResponseEntity.ok().body(new ApiRespDTO<FamilyInfoDTO>(true, "GET_SUCCESS", "Get Success",
                                familyService.getFamily(me.getUser().getUserId())));
        }

        @PostMapping("/subscribe")
        public ResponseEntity<ApiRespDTO<?>> subscribePlan(@RequestBody SubscriptionPlanDTO plan,
                        @AuthenticationPrincipal CustomUserDetail me) {
                return ResponseEntity.ok()
                                .body(new ApiRespDTO<FamilyInfoDTO>(true, "SUBSCRIBE_SUCCESS",
                                                "Subscription successful",
                                                familyService.subscribePlan(plan, me.getUser().getUserId())));
        }

        @PostMapping("/library/share")
        public ResponseEntity<ApiRespDTO<?>> shareGames(@RequestBody ShareGamesDTO dto,
                        @AuthenticationPrincipal CustomUserDetail me) {

                return ResponseEntity.ok()
                                .body(new ApiRespDTO<FamilyInfoDTO>(true, "SHARE_GAMES_SUCCESS",
                                                "Games shared successfully",
                                                familyService.shareGames(dto, me.getUser().getUserId())));
        }

        @PostMapping("/library/remove")
        public ResponseEntity<ApiRespDTO<?>> removeGameFromLibrary(@RequestBody ShareGamesDTO dto,
                        @AuthenticationPrincipal CustomUserDetail me) {
                return ResponseEntity.ok()
                                .body(new ApiRespDTO<FamilyInfoDTO>(true, "REMOVE_GAME_SUCCESS",
                                                "Game removed successfully",
                                                familyService.removeGameFromLibrary(dto, me.getUser().getUserId())));
        }

        @PostMapping("/invite/{friendId}")
        public ResponseEntity<ApiRespDTO<?>> sendInvite(@PathVariable Long friendId,
                        @AuthenticationPrincipal CustomUserDetail me) {
                return ResponseEntity.ok()
                                .body(new ApiRespDTO<FamilyInvitationDTO>(true, "INVITE_SENT",
                                                "Invite sent successfully",
                                                familyService.sendInvite(friendId, me.getUser().getUserId())));
        }

        @PostMapping("/invite")
        public ResponseEntity<ApiRespDTO<?>> sendInvite(@RequestBody List<Long> friendIds,
                        @AuthenticationPrincipal CustomUserDetail me) {
                // This method is not implemented in the original code, but it seems to be
                // intended to send invites to multiple friends at once.
                List<FamilyInvitationDTO> invitations = familyService.sendInvites(friendIds, me.getUser().getUserId());
                return ResponseEntity.ok()
                                .body(new ApiRespDTO<>(true, "INVITES_SENT",
                                                "Invites sent successfully", invitations));
        }

        @GetMapping("/invitation")
        public ResponseEntity<ApiRespDTO<?>> getInvitations(@AuthenticationPrincipal CustomUserDetail me) {
                return ResponseEntity.ok()
                                .body(new ApiRespDTO<List<FamilyInvitationDTO>>(true, "GET_INVITATIONS_SUCCESS",
                                                "Invitations retrieved successfully",
                                                familyService.getInvitations(me.getUser().getUserId())));
        }

        @GetMapping("/invitation/sent")
        public ResponseEntity<ApiRespDTO<?>> getSentInvitations(@AuthenticationPrincipal CustomUserDetail me) {
                return ResponseEntity.ok().body(new ApiRespDTO<List<FamilyInvitationDTO>>(true,
                                "GET_SENT_INVITATIONS_SUCCESS", "Sent invitations retrieved successfully",
                                familyService.getSentInvitations(me.getUser().getUserId())));
        }

        @PostMapping("/invitation/accept/{inviteId}")
        public ResponseEntity<ApiRespDTO<?>> acceptInvitation(@PathVariable Long inviteId,
                        @AuthenticationPrincipal CustomUserDetail me) {
                familyService.acceptInvitation(inviteId, me.getUser().getUserId());

                return ResponseEntity.ok()
                                .body(new ApiRespDTO<FamilyInfoDTO>(true, "INVITATION_ACCEPTED",
                                                "Invitation accepted successfully",
                                                familyService.getFamily(me.getUser().getUserId())));
        }

        @PostMapping("/invitation/reject/{inviteId}")
        public ResponseEntity<ApiRespDTO<?>> rejectInvitation(@PathVariable Long inviteId,
                        @AuthenticationPrincipal CustomUserDetail me) {
                familyService.rejectInvitation(inviteId, me.getUser().getUserId());
                return ResponseEntity.ok()
                                .body(new ApiRespDTO<>(true, "INVITATION_REJECTED",
                                                "Invitation rejected successfully", null));
        }

        @DeleteMapping("/invitation/cancel/{inviteId}")
        public ResponseEntity<ApiRespDTO<?>> deleteInvitation(@PathVariable Long inviteId,
                        @AuthenticationPrincipal CustomUserDetail me) {
                familyService.deleteInvitation(inviteId, me.getUser().getUserId());
                return ResponseEntity.ok()
                                .body(new ApiRespDTO<>(true, "INVITATION_DELETED",
                                                "Invitation deleted successfully", null));
        }

        @DeleteMapping("/leave")
        public ResponseEntity<ApiRespDTO<?>> leaveFamily(@AuthenticationPrincipal CustomUserDetail me) {
                familyService.leaveFamily(me.getUser().getUserId());
                return ResponseEntity.ok()
                                .body(new ApiRespDTO<>(true, "LEAVE_FAMILY_SUCCESS",
                                                "You have left the family successfully", null));
        }

        @PostMapping("/available-friends")
        public ResponseEntity<ApiRespDTO<?>> getAvailableFriends(@RequestBody List<Long> friendIds,
                        @AuthenticationPrincipal CustomUserDetail me) {
                List<Long> availableFriends = familyService.getAvailableFriends(friendIds, me.getUser().getUserId());
                return ResponseEntity.ok()
                                .body(new ApiRespDTO<>(true, "GET_AVAILABLE_FRIENDS_SUCCESS",
                                                "Available friends retrieved successfully", availableFriends));
        }

        @DeleteMapping("/remove-member/{memberId}")
        public ResponseEntity<ApiRespDTO<?>> removeFamilyMember(@PathVariable Long memberId,
                        @AuthenticationPrincipal CustomUserDetail me) {
                familyService.removeFamilyMember(memberId, me.getUser().getUserId());
                return ResponseEntity.ok()
                                .body(new ApiRespDTO<>(true, "REMOVE_MEMBER_SUCCESS",
                                                "Family member removed successfully", null));
        }

        @PostMapping("/remove-member")
        public ResponseEntity<ApiRespDTO<?>> removeFamilyMembers(@RequestBody List<Long> memberIds,
                        @AuthenticationPrincipal CustomUserDetail me) {
                familyService.removeFamilyMember(memberIds, me.getUser().getUserId());
                return ResponseEntity.ok()
                                .body(new ApiRespDTO<>(true, "REMOVE_MEMBER_SUCCESS",
                                                "Family member removed successfully", null));
        }

        @DeleteMapping("/delete")
        public ResponseEntity<ApiRespDTO<?>> deleteFamily(@AuthenticationPrincipal CustomUserDetail me) {
                familyService.deleteFamily(me.getUser().getUserId());
                return ResponseEntity.ok()
                                .body(new ApiRespDTO<>(true, "DELETE_FAMILY_SUCCESS",
                                                "Family deleted successfully", null));
        }
}
