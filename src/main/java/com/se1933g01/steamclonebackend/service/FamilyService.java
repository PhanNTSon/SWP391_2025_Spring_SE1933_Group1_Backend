package com.se1933g01.steamclonebackend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.se1933g01.steamclonebackend.dto.MediaDTO;
import com.se1933g01.steamclonebackend.dto.family.FamilyGameDTO;
import com.se1933g01.steamclonebackend.dto.family.FamilyInfoDTO;
import com.se1933g01.steamclonebackend.dto.family.FamilyMemberDTO;
import com.se1933g01.steamclonebackend.dto.family.ShareGamesDTO;
import com.se1933g01.steamclonebackend.dto.family.SubscriptionPlanDTO;
import com.se1933g01.steamclonebackend.entity.community.family.Family;
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

    public FamilyService(FamilyRepo familyRepo, FamilyInvitationRepo familyInvitationRepo,
            FamilyMemberRepo familyMemberRepo, FamilyLibraryRepo familyLibraryRepo, EntityManager entityManager) {
        this.familyRepo = familyRepo;
        this.familyInvitationRepo = familyInvitationRepo;
        this.familyMemberRepo = familyMemberRepo;
        this.familyLibraryRepo = familyLibraryRepo;
        this.entityManager = entityManager;
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
    public FamilyInfoDTO removeGameFromLibrary(ShareGamesDTO dto, Long userId){
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
}
