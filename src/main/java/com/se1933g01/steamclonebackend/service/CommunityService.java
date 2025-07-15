package com.se1933g01.steamclonebackend.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.se1933g01.steamclonebackend.dto.ChatMessageDTO;
import com.se1933g01.steamclonebackend.dto.community.ConversationDTO;
import com.se1933g01.steamclonebackend.dto.community.FriendDTO;
import com.se1933g01.steamclonebackend.dto.community.FriendshipDTO;
import com.se1933g01.steamclonebackend.dto.community.InviteDTO;
import com.se1933g01.steamclonebackend.dto.community.MessageDTO;
import com.se1933g01.steamclonebackend.dto.community.SearchResult;
import com.se1933g01.steamclonebackend.entity.community.Conversation;
import com.se1933g01.steamclonebackend.entity.community.Friendship;
import com.se1933g01.steamclonebackend.entity.community.FriendshipId;
import com.se1933g01.steamclonebackend.entity.community.Message;
import com.se1933g01.steamclonebackend.entity.user.User;
import com.se1933g01.steamclonebackend.repository.ConversationRepo;
import com.se1933g01.steamclonebackend.repository.FriendshipRepo;
import com.se1933g01.steamclonebackend.repository.MessageRepo;
import com.se1933g01.steamclonebackend.repository.UserRepo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * @author Phan NT Son
 * @since 23-06-2025
 */
@Service
public class CommunityService {
    @PersistenceContext
    private final EntityManager entityManager;

    private final FriendshipRepo friendshipRepo;
    private final ConversationRepo conversationRepo;
    private final MessageRepo messageRepo;
    private final UserRepo userRepo;
    private final SimpMessagingTemplate simp;

    private final static String FRIEND_INIVATIONS_CHANNEL = "/queue/friend.invitations";

    public CommunityService(EntityManager entityManager, FriendshipRepo friendshipRepo,
            ConversationRepo conversationRepo, MessageRepo messageRepo, UserRepo userRepo, SimpMessagingTemplate simp) {
        this.entityManager = entityManager;
        this.friendshipRepo = friendshipRepo;
        this.conversationRepo = conversationRepo;
        this.messageRepo = messageRepo;
        this.userRepo = userRepo;
        this.simp = simp;
    }

    /**
     * Send an invite to a User by Creating a new Friendship in DB
     * with Status = "Pending"
     * 
     * @param userId
     * @param friendId
     * @return
     */
    @Transactional
    public FriendshipDTO sendInvite(long userId, long friendId) {
        User curUser = entityManager.getReference(User.class, userId);
        User friend = entityManager.getReference(User.class, friendId);

        FriendshipId id = new FriendshipId(curUser.getUserId(), friend.getUserId());

        Friendship newFriendship = new Friendship(id, curUser, friend, "Pending", LocalDate.now());
        friendshipRepo.save(newFriendship);

        InviteDTO invitation = new InviteDTO(userId, curUser.getAvatarUrl(), curUser.getUsername());
        simp.convertAndSendToUser(friend.getUsername(), FRIEND_INIVATIONS_CHANNEL, invitation);

        return new FriendshipDTO(userId, friendId, "Pending", newFriendship.getCreatedAt());
    }

    /**
     * Get all Friendship in DB that have friendId = userId with status = "Pending"
     * 
     * @param userId
     * @return
     */
    public List<InviteDTO> getInviteFromFriend(long userId) {
        List<Friendship> queryResult = friendshipRepo.findAllInviteFromFriend(userId);
        return queryResult.stream().map(friendship -> new InviteDTO(
                friendship.getFriendshipId().getUserId(),
                friendship.getUser().getAvatarUrl(),
                friendship.getUser().getUsername())).toList();
    }

    /**
     * Get all Friendship in DB that have userId = userid with status "Pending"
     * 
     * @param userId
     * @return
     */
    public List<InviteDTO> getInviteFromUser(long userId) {
        List<Friendship> queryResult = friendshipRepo.findAllInviteFromUser(userId);
        return queryResult.stream().map(friendship -> new InviteDTO(
                friendship.getFriendshipId().getFriendId(),
                friendship.getFriend().getAvatarUrl(),
                friendship.getFriend().getUsername())).toList();
    }

    /**
     * First, create new Friendship with userId = userId and friendId = friendId
     * Then, update Friendship with userId = friendId and friendId = userId status
     * from "Pending" to "Accepted"
     * 
     * Reason is that 2 users are friends when there are 2 Records in DB:
     * User → Friend & Friend → User with status: "Accepted"
     * 
     * @param userId
     * @param friendId
     * @return
     */
    @Transactional
    public FriendshipDTO acceptInvite(long userId, long friendId) {
        // Get references
        User curUser = entityManager.getReference(User.class, userId);
        User friend = entityManager.getReference(User.class, friendId);

        // Create new Friendship
        FriendshipId newId = new FriendshipId(userId, friendId);
        Friendship newT = friendshipRepo.findById(newId).orElse(null);

        if (newT == null) {
            newT = new Friendship(newId, curUser, friend, "Accepted", LocalDate.now());
        } else {
            newT.setCreatedAt(LocalDate.now());
            newT.setStatus("Accepted");
        }

        // Update already Friendship
        FriendshipId inviteId = new FriendshipId(friendId, userId);
        Friendship inviteObj = friendshipRepo.findById(inviteId).orElse(null);
        inviteObj.setStatus("Accepted");
        inviteObj.setCreatedAt(LocalDate.now());

        // Save to DB
        friendshipRepo.save(newT);
        friendshipRepo.save(inviteObj);

        Conversation con = (userId < friendId) ? conversationRepo.findByUser1AndUser2(userId, friendId)
                : conversationRepo.findByUser1AndUser2(friendId, userId);
        if (con == null) {
            this.createConversation(userId, friendId);
        }

        return new FriendshipDTO(userId, friendId, "Accepted", LocalDate.now());
    }

    /**
     * Same as accept invite but with status "blocked"
     * 
     * @param userId
     * @param friendId
     * @return
     */
    @Transactional
    public FriendshipDTO blockInvite(long userId, long friendId) {
        // Get references
        User curUser = entityManager.getReference(User.class, userId);
        User friend = entityManager.getReference(User.class, friendId);

        // Create new Friendship
        FriendshipId newId = new FriendshipId(userId, friendId);
        Friendship newT = new Friendship(newId, curUser, friend, "Blocked", LocalDate.now());

        // Update already Friendship
        FriendshipId inviteId = new FriendshipId(friendId, userId);
        Friendship inviteObj = friendshipRepo.findById(inviteId).orElse(null);
        inviteObj.setStatus("Blocked");
        inviteObj.setCreatedAt(LocalDate.now());

        // Save to DB
        friendshipRepo.save(newT);
        friendshipRepo.save(inviteObj);

        return new FriendshipDTO(userId, friendId, "Blocked", LocalDate.now());
    }

    /**
     * Delete a Friendship data base on userId and friendId, find both combination
     * (userId, friendId) & (friendId, userId)
     * 
     * @param userId
     * @param friendId
     */
    @Transactional
    public void deleteFriendship(long userId, long friendId) {
        Friendship res = friendshipRepo.findById(new FriendshipId(userId, friendId)).orElse(null);
        Friendship reverseRes = friendshipRepo.findById(new FriendshipId(friendId, userId)).orElse(null);

        if (res != null) {
            friendshipRepo.delete(res);
        }

        if (reverseRes != null) {
            friendshipRepo.delete(reverseRes);
        }

    }

    /**
     * Get list FriendDTO from a list of Friendship found in DB that have
     * current userId.
     * 
     * @param userId
     * @return
     */
    public List<FriendDTO> getFriendList(long userId) {
        return friendshipRepo.findAllFriend(userId).stream().map(friendship -> new FriendDTO(
                friendship.getFriend().getUserId(),
                friendship.getFriend().getUsername())).toList();
    }

    /**
     * Create a Conversation and save to DB. Make sure userId < friendId
     * 
     * @param userId
     * @param friendId
     */
    @Transactional
    public void createConversation(long userId, long friendId) {
        User curUser = entityManager.getReference(User.class, userId);
        User friend = entityManager.getReference(User.class, friendId);

        Conversation newConversation = new Conversation();
        newConversation.setUser1((userId < friendId) ? curUser : friend);
        newConversation.setUser2((userId < friendId) ? friend : curUser);
        newConversation.setCreatedAt(LocalDate.now());

        conversationRepo.save(newConversation);
    }

    public ConversationDTO getConversation(long userId, long friendId) {
        Conversation conversation = (userId > friendId) ? (conversationRepo.findByUser1AndUser2(friendId, userId))
                : (conversationRepo.findByUser1AndUser2(userId, friendId));

        long conversationId = conversation.getConversationId();
        List<Message> messages = messageRepo.findAllByConversationId(conversationId);

        return new ConversationDTO(conversationId,
                messages.stream().map(message -> new MessageDTO(
                        message.getSender().getUsername(),
                        message.getMessageContent(),
                        message.getSentAt())).toList());
    }

    @Transactional
    public void saveMessage(ChatMessageDTO msg, String username) {
        Message nMessage = new Message();
        Conversation conver = entityManager.getReference(Conversation.class, msg.getConversationId());
        User sender = userRepo.findByUsername(username).orElse(null);

        nMessage.setConversation(conver);
        nMessage.setSender(sender);
        nMessage.setMessageContent(msg.getContent());
        nMessage.setSentAt(msg.getSentAt());

        messageRepo.save(nMessage);
    }

    public SearchResult findUser(long friendId) throws Exception {
        User target = userRepo.findById(friendId).orElse(null);
        if (target == null) {
            throw new Exception("No User found");
        } else {
            return new SearchResult(target.getUserId(), target.getUsername(), target.getAvatarUrl());
        }
    }
}
