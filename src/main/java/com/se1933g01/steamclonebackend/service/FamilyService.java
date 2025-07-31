package com.se1933g01.steamclonebackend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.se1933g01.steamclonebackend.dto.MediaDTO;
import com.se1933g01.steamclonebackend.dto.family.FamilyGameDTO;
import com.se1933g01.steamclonebackend.dto.family.FamilyInfoDTO;
import com.se1933g01.steamclonebackend.dto.family.FamilyInvitationDTO;
import com.se1933g01.steamclonebackend.dto.family.FamilyMemberDTO;
import com.se1933g01.steamclonebackend.dto.family.ShareGamesDTO;
import com.se1933g01.steamclonebackend.dto.family.SubscriptionPlanDTO;
import com.se1933g01.steamclonebackend.entity.community.family.Family;
import com.se1933g01.steamclonebackend.entity.community.family.FamilyInvitation;
import com.se1933g01.steamclonebackend.entity.community.family.FamilyLibrary;
import com.se1933g01.steamclonebackend.entity.community.family.FamilyLibraryId;
import com.se1933g01.steamclonebackend.entity.community.family.FamilyMember;
import com.se1933g01.steamclonebackend.entity.community.family.FamilyMemberId;
import com.se1933g01.steamclonebackend.entity.community.family.SubscriptionPlan;
import com.se1933g01.steamclonebackend.entity.game.Game;
import com.se1933g01.steamclonebackend.entity.user.User;
import com.se1933g01.steamclonebackend.repository.FamilyInvitationRepo;
import com.se1933g01.steamclonebackend.repository.FamilyLibraryRepo;
import com.se1933g01.steamclonebackend.repository.FamilyMemberRepo;
import com.se1933g01.steamclonebackend.repository.FamilyRepo;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Service
public class FamilyService {

    private final FamilyRepo familyRepo;
    private final FamilyInvitationRepo familyInvitationRepo;
    private final FamilyMemberRepo familyMemberRepo;
    private final FamilyLibraryRepo familyLibraryRepo;
    private final EntityManager entityManager;
    private final SimpMessagingTemplate simp;

    private final String FAMILY_INVITATION_CHANNEL = "/queue/family/invitation";

    public FamilyService(FamilyRepo familyRepo, FamilyInvitationRepo familyInvitationRepo,
            FamilyMemberRepo familyMemberRepo, FamilyLibraryRepo familyLibraryRepo, EntityManager entityManager,
            SimpMessagingTemplate simp) {
        this.familyRepo = familyRepo;
        this.familyInvitationRepo = familyInvitationRepo;
        this.familyMemberRepo = familyMemberRepo;
        this.familyLibraryRepo = familyLibraryRepo;
        this.entityManager = entityManager;
        this.simp = simp;
    }

    private void sendToUser(String username, Object message) {
        simp.convertAndSendToUser(username, FAMILY_INVITATION_CHANNEL, message);
    }

    public FamilyInfoDTO getFamily(Long userId) {
        Optional<Family> optOwned = familyRepo.findByOwner(userId);
        Family family = null;

        if (optOwned.isPresent()) {
            family = optOwned.get();
        } else {
            // 2. Check membership (non-owner)
            Optional<FamilyMember> membership = familyMemberRepo.findByUserId(userId);
            if (membership.isPresent()) {
                family = membership.get().getFamily();
            }
        }

        // 3. Nếu không có family nào
        if (family == null) {
            return FamilyInfoDTO.builder()
                    .familyId(-1L)
                    .build();
        }

        // 4. Build DTO
        boolean isOwner = family.getOwner().getUserId().equals(userId);
        List<FamilyMemberDTO> members = new ArrayList<>();
        for (FamilyMember m : family.getMembers()) {
            User u = m.getUser();
            members.add(new FamilyMemberDTO(u.getUserId(), u.getUsername(), u.getAvatarUrl(), m.isOwner()));
        }

        boolean isPlayable = family.getExpDate().isAfter(LocalDate.now());

        List<FamilyGameDTO> games = new ArrayList<>();
        for (FamilyLibrary lib : family.getSharedGames()) {
            Game game = lib.getGame();
            FamilyGameDTO gameDTO = FamilyGameDTO.builder()
                    .id(game.getGameId())
                    .name(game.getName())
                    .gameUrl(game.getGameUrl())
                    .isPlayable(isPlayable)
                    .media(game.getMedia().stream().map(m -> new MediaDTO(m.getMediaId(), m.getUrl(), m.getType()))
                            .toList())
                    .build();

            games.add(gameDTO);
        }

        // 5. Trả về
        return FamilyInfoDTO.builder()
                .familyId(family.getFamilyId())
                .ownerId(family.getOwner().getUserId())
                .expDate(family.getExpDate())
                .isOwner(isOwner)
                .members(members)
                .games(games)
                .build();

    }

    @Transactional
    public FamilyInfoDTO subscribePlan(SubscriptionPlanDTO plan, Long userId) {
        Optional<Family> optOwned = familyRepo.findByOwner(userId);
        Family family;

        if (optOwned.isPresent()) {
            family = optOwned.get();
        } else {
            family = new Family();
            family.setOwner(entityManager.getReference(User.class, userId));
            family.setCreatedAt(LocalDate.now());
            family.setExpDate(LocalDate.now().plusDays(plan.getDuration()));
            entityManager.persist(family);

            // Thêm user vào bảng FamilyMember luôn (với role là OWNER)
            FamilyMemberId memberId = new FamilyMemberId(family.getFamilyId(), userId);
            FamilyMember ownerMember = new FamilyMember();
            ownerMember.setId(memberId);
            ownerMember.setFamily(family);
            ownerMember.setUser(family.getOwner());
            ownerMember.setOwner(true);
            ownerMember.setJoinedAt(LocalDate.now());
            entityManager.persist(ownerMember);
        }

        // Tính lại expDate nếu đã có family
        if (optOwned.isPresent()) {
            LocalDate newExpDate = family.getExpDate().isAfter(LocalDate.now())
                    ? family.getExpDate().plusDays(plan.getDuration())
                    : LocalDate.now().plusDays(plan.getDuration());
            family.setExpDate(newExpDate);
            // Không cần persist lại nếu family đã managed
        }

        // 4. Lưu SubscriptionPlan
        SubscriptionPlan subEntity = new SubscriptionPlan();
        subEntity.setFamilyId(family.getFamilyId());
        subEntity.setPlanName(plan.getPlanName());
        subEntity.setPrice(plan.getPrice());
        subEntity.setDurationInDays(plan.getDuration());

        // Thiết lập ngày bắt đầu & kết thúc
        LocalDate now = LocalDate.now();
        LocalDate startAt = family.getExpDate().isAfter(now)
                ? family.getExpDate().minusDays(plan.getDuration()) // nếu đang còn hạn, thì startAt = ngày nối tiếp
                : now;
        LocalDate endAt = startAt.plusDays(plan.getDuration());

        subEntity.setStartAt(startAt);
        subEntity.setEndAt(endAt);
        subEntity.setCreatedAt(now);

        entityManager.persist(subEntity);

        return FamilyInfoDTO.builder()
                .familyId(family.getFamilyId())
                .ownerId(family.getOwner().getUserId())
                .expDate(family.getExpDate())
                .isOwner(true)
                .members(Collections.emptyList())
                .games(Collections.emptyList())
                .build();
    }

    @Transactional
    public FamilyInfoDTO shareGames(ShareGamesDTO dto, Long userId) {
        Optional<Family> optOwned = familyRepo.findByOwner(userId);
        Family family;

        if (optOwned.isPresent()) {
            family = optOwned.get();
        } else {
            throw new IllegalStateException("User is not the owner of any family.");
        }

        // 2. Lấy danh sách game từ DTO
        List<Long> gameIds = dto.getGameIds();
        List<Game> gamesToShare = entityManager.createQuery("SELECT g FROM Game g WHERE g.gameId IN :ids", Game.class)
                .setParameter("ids", gameIds)
                .getResultList();

        // 3. Tạo FamilyLibrary cho mỗi game
        for (Game game : gamesToShare) {
            FamilyLibrary libraryEntry = new FamilyLibrary();
            FamilyLibraryId libraryId = new FamilyLibraryId(family.getFamilyId(), game.getGameId());
            libraryEntry.setId(libraryId);
            libraryEntry.setFamily(family);
            libraryEntry.setGame(game);
            entityManager.persist(libraryEntry);
        }

        // 4. Trả về thông tin gia đình đã cập nhật
        return getFamily(userId);
    }

    @Transactional
    public FamilyInfoDTO removeGameFromLibrary(ShareGamesDTO dto, Long userId) {
        Optional<Family> optOwned = familyRepo.findByOwner(userId);
        Family family;

        if (optOwned.isPresent()) {
            family = optOwned.get();
        } else {
            throw new IllegalStateException("User is not the owner of any family.");
        }

        // 2. Lấy danh sách game từ DTO
        List<Long> gameIds = dto.getGameIds();

        // 3. Xoá FamilyLibrary cho mỗi game
        for (Long gameId : gameIds) {
            FamilyLibraryId libraryId = new FamilyLibraryId(family.getFamilyId(), gameId);
            familyLibraryRepo.delete(entityManager.getReference(FamilyLibrary.class, libraryId));
        }

        // 4. Trả về thông tin gia đình đã cập nhật
        return getFamily(userId);
    }

    public List<FamilyInvitationDTO> getInvitations(Long userId) {
        // 1. Lấy danh sách lời mời của người dùng
        List<FamilyInvitation> invitations = familyInvitationRepo.findAll().stream()
                .filter(inv -> inv.getReceiver().getUserId().equals(userId))
                .collect(Collectors.toList());

        // 2. Chuyển đổi sang DTO
        return invitations.stream()
                .map(inv -> FamilyInvitationDTO.builder()
                        .inviteId(inv.getInviteID())
                        .senderId(inv.getInvitor().getUserId())
                        .receiverId(inv.getReceiver().getUserId())
                        .senderName(inv.getInvitor().getUsername())
                        .receiverName(inv.getReceiver().getUsername())
                        .senderAvatar(inv.getInvitor().getAvatarUrl())
                        .receiverAvatar(inv.getReceiver().getAvatarUrl())
                        .createdAt(inv.getCreatedAt())
                        .expiresAt(inv.getExpiresAt())
                        .build())
                .collect(Collectors.toList());
    }

    public List<FamilyInvitationDTO> getSentInvitations(Long userId) {
        // 1. Lấy danh sách lời mời đã gửi của người dùng
        List<FamilyInvitation> sentInvitations = familyInvitationRepo.findAll().stream()
                .filter(inv -> inv.getInvitor().getUserId().equals(userId))
                .collect(Collectors.toList());
        // 2. Chuyển đổi sang DTO
        return sentInvitations.stream()
                .map(inv -> FamilyInvitationDTO.builder()
                        .inviteId(inv.getInviteID())
                        .senderId(inv.getInvitor().getUserId())
                        .receiverId(inv.getReceiver().getUserId())
                        .senderName(inv.getInvitor().getUsername())
                        .receiverName(inv.getReceiver().getUsername())
                        .senderAvatar(inv.getInvitor().getAvatarUrl())
                        .receiverAvatar(inv.getReceiver().getAvatarUrl())
                        .createdAt(inv.getCreatedAt())
                        .expiresAt(inv.getExpiresAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public FamilyInvitationDTO sendInvite(Long friendId, Long userId) {
        Optional<Family> optOwned = familyRepo.findByOwner(userId);
        Family family;

        if (optOwned.isPresent()) {
            family = optOwned.get();
        } else {
            throw new IllegalStateException("User is not the owner of any family.");
        }

        // 2. Kiểm tra xem đã có lời mời nào chưa
        familyInvitationRepo.findByFamilyAndFriend(family.getFamilyId(), friendId)
                .ifPresent(invitation -> {
                    throw new IllegalStateException("Invitation already exists for this family and friend.");
                });

        // 3. Check if friend is already a member of other family
        Optional<FamilyMember> existingMember = familyMemberRepo.findByUserId(friendId);
        if (existingMember.isPresent()) {
            throw new IllegalStateException("Friend is already a member of another family.");
        }

        // 4. Tạo lời mời mới
        User invitor = entityManager.getReference(User.class, userId);
        User receiver = entityManager.getReference(User.class, friendId);
        FamilyInvitation invitation = new FamilyInvitation();
        invitation.setFamily(family);
        invitation.setInvitor(invitor);
        invitation.setReceiver(receiver);
        invitation.setCreatedAt(LocalDate.now());
        invitation.setExpiresAt(LocalDate.now().plusDays(7)); // Lời mời có hiệu lực trong 7 ngày
        entityManager.persist(invitation);

        return FamilyInvitationDTO.builder()
                .inviteId(invitation.getInviteID())
                .senderId(invitor.getUserId())
                .receiverId(receiver.getUserId())
                .createdAt(invitation.getCreatedAt())
                .expiresAt(invitation.getExpiresAt())
                .build();
    }

    @Transactional
    public List<FamilyInvitationDTO> sendInvites(List<Long> friendIds, Long userId) {
        Optional<Family> optOwned = familyRepo.findByOwner(userId);
        Family family;

        if (optOwned.isPresent()) {
            family = optOwned.get();
        } else {
            throw new IllegalStateException("User is not the owner of any family.");
        }

        List<FamilyInvitationDTO> sentInvitations = new ArrayList<>();

        for (Long friendId : friendIds) {
            // 2. Kiểm tra xem đã có lời mời nào chưa
            familyInvitationRepo.findByFamilyAndFriend(family.getFamilyId(), friendId)
                    .ifPresent(invitation -> {
                        throw new IllegalStateException("Invitation already exists for this family and friend.");
                    });
            // 3. Check if friend is already a member of other family
            Optional<FamilyMember> existingMember = familyMemberRepo.findByUserId(friendId);
            if (existingMember.isPresent()) {
                throw new IllegalStateException("Friend is already a member of another family.");
            }
            // 4. Tạo lời mời mới
            User invitor = entityManager.getReference(User.class, userId);
            User receiver = entityManager.getReference(User.class, friendId);
            FamilyInvitation invitation = new FamilyInvitation();
            invitation.setFamily(family);
            invitation.setInvitor(invitor);
            invitation.setReceiver(receiver);
            invitation.setCreatedAt(LocalDate.now());
            invitation.setExpiresAt(LocalDate.now().plusDays(7));
            entityManager.persist(invitation);

            // 5. Chuyển đổi sang DTO
            FamilyInvitationDTO dto = FamilyInvitationDTO.builder()
                    .inviteId(invitation.getInviteID())
                    .senderId(invitor.getUserId())
                    .receiverId(receiver.getUserId())
                    .createdAt(invitation.getCreatedAt())
                    .expiresAt(invitation.getExpiresAt())
                    .build();
            sentInvitations.add(dto);
        }

        return sentInvitations;
    }

    public FamilyInvitationDTO acceptInvitation(Long inviteId, Long userId) {
        // 1. Lấy lời mời
        FamilyInvitation invitation = familyInvitationRepo.findById(inviteId)
                .orElseThrow(() -> new IllegalStateException("Invitation not found."));

        // 2. Kiểm tra xem người dùng có phải là người nhận không
        if (!invitation.getReceiver().getUserId().equals(userId)) {
            throw new IllegalStateException("You are not the receiver of this invitation.");
        }

        // 3. Thêm người dùng vào gia đình
        Family family = invitation.getFamily();
        FamilyMemberId memberId = new FamilyMemberId(family.getFamilyId(), userId);
        FamilyMember newMember = new FamilyMember();
        newMember.setId(memberId);
        newMember.setFamily(family);
        newMember.setUser(entityManager.getReference(User.class, userId));
        newMember.setOwner(false); // Mặc định là thành viên, không phải chủ sở hữu
        newMember.setJoinedAt(LocalDate.now());
        entityManager.persist(newMember);
        // 4. Xoá lời mời
        familyInvitationRepo.delete(invitation);
        // 5. Trả về thông tin lời mời đã chấp nhận
        return FamilyInvitationDTO.builder()
                .inviteId(invitation.getInviteID())
                .senderId(invitation.getInvitor().getUserId())
                .receiverId(invitation.getReceiver().getUserId())
                .createdAt(invitation.getCreatedAt())
                .expiresAt(invitation.getExpiresAt())
                .build();

    }

    @Transactional
    public void deleteInvitation(Long inviteId, Long userId) {
        // 1. Lấy lời mời
        FamilyInvitation invitation = familyInvitationRepo.findById(inviteId)
                .orElseThrow(() -> new IllegalStateException("Invitation not found."));

        // 2. Xoá lời mời
        familyInvitationRepo.delete(invitation);
    }

    @Transactional
    public void rejectInvitation(Long inviteId, Long userId) {
        // 1. Lấy lời mời
        FamilyInvitation invitation = familyInvitationRepo.findById(inviteId)
                .orElseThrow(() -> new IllegalStateException("Invitation not found."));

        // 2. Kiểm tra xem người dùng có phải là người nhận không
        if (!invitation.getReceiver().getUserId().equals(userId)) {
            throw new IllegalStateException("You are not the receiver of this invitation.");
        }

        // 3. Xoá lời mời
        familyInvitationRepo.delete(invitation);
    }

    @Transactional
    public void leaveFamily(Long userId) {
        // 1. Tìm FamilyMember của người dùng
        Optional<FamilyMember> memberOpt = familyMemberRepo.findByUserId(userId);
        if (memberOpt.isEmpty()) {
            throw new IllegalStateException("You are not a member of any family.");
        }

        FamilyMember member = memberOpt.get();

        // 2. Nếu là chủ sở hữu, không thể rời khỏi gia đình
        if (member.isOwner()) {
            throw new IllegalStateException("You cannot leave the family as you are the owner.");
        }

        // 3. Xoá FamilyMember
        familyMemberRepo.delete(member);

    }

    public List<Long> getAvailableFriends(List<Long> friendIds, Long userId) {

        // 1. Check if friends are already in a family
        List<Long> availableFriends = friendIds.stream()
                .filter(friendId -> !familyMemberRepo.findByUserId(friendId).isPresent())
                .collect(Collectors.toList());

        // 2. Check if friends have already been invited to the family
        List<Long> alreadyInvitedFriends = familyInvitationRepo.findAll().stream()
                .filter(inv -> inv.getFamily().getOwner().getUserId().equals(userId))
                .map(inv -> inv.getReceiver().getUserId())
                .collect(Collectors.toList());

        // 3. Loại bỏ những người đã được mời
        availableFriends = availableFriends.stream()
                .filter(friendId -> !alreadyInvitedFriends.contains(friendId))
                .collect(Collectors.toList());

        return availableFriends;
    }

    @Transactional
    public void removeFamilyMember(Long memberId, Long userId) {
        // 1. Tìm FamilyMember của người dùng
        Optional<FamilyMember> memberOpt = familyMemberRepo.findByUserId(memberId);
        if (memberOpt.isEmpty()) {
            throw new IllegalStateException("Member not found in any family.");
        }
        FamilyMember member = memberOpt.get();

        // 2. Kiểm tra xem người dùng có quyền xoá thành viên này không
        Optional<Family> familyOpt = familyRepo.findByOwner(userId);
        if (familyOpt.isEmpty() || !familyOpt.get().getFamilyId().equals(member.getFamily().getFamilyId())) {
            throw new IllegalStateException("You do not have permission to remove this member.");
        }

        // 3. Nếu là chủ sở hữu, không thể xoá
        if (member.isOwner()) {
            throw new IllegalStateException("You cannot remove the owner of the family.");
        }

        // 4. Kiểm tra xem có phải là thành viên của gia đình không
        if (!familyMemberRepo.findByFamilyAndUser(member.getFamily().getFamilyId(), memberId).isPresent()) {
            throw new IllegalStateException("Member is not part of the family.");
        }

        // 5. Xoá FamilyMember
        familyMemberRepo.delete(member);
    }
}
