package com.se1933g01.steamclonebackend.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.se1933g01.steamclonebackend.dto.ChatMessageDTO;
import com.se1933g01.steamclonebackend.dto.community.ConversationDTO;
import com.se1933g01.steamclonebackend.dto.community.FriendRequestDTO;
import com.se1933g01.steamclonebackend.dto.community.MessageDTO;
import com.se1933g01.steamclonebackend.dto.community.SearchResult;
import com.se1933g01.steamclonebackend.dto.user.FriendDTO;
import com.se1933g01.steamclonebackend.entity.community.Block;
import com.se1933g01.steamclonebackend.entity.community.Conversation;
import com.se1933g01.steamclonebackend.entity.community.FriendRequest;
import com.se1933g01.steamclonebackend.entity.community.Friendship;
import com.se1933g01.steamclonebackend.entity.community.FriendshipId;
import com.se1933g01.steamclonebackend.entity.community.Message;
import com.se1933g01.steamclonebackend.entity.user.User;
import com.se1933g01.steamclonebackend.repository.BlockRepo;
import com.se1933g01.steamclonebackend.repository.ConversationRepo;
import com.se1933g01.steamclonebackend.repository.FriendRequestRepo;
import com.se1933g01.steamclonebackend.repository.FriendshipRepo;
import com.se1933g01.steamclonebackend.repository.MessageRepo;
import com.se1933g01.steamclonebackend.repository.UserRepo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
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
    private final FriendRequestRepo friendRequestRepo;
    private final BlockRepo blockRepo;
    private final ConversationRepo conversationRepo;
    private final MessageRepo messageRepo;
    private final UserRepo userRepo;
    private final SimpMessagingTemplate simp;

    private final static String FRIEND_INIVATIONS_CHANNEL = "/queue/friend.invitations";
    private final static String FRIEND_REQUEST_ACTION_CHANNEL = "/queue/friend.request";

    public CommunityService(EntityManager entityManager, FriendshipRepo friendshipRepo,
            FriendRequestRepo friendRequestRepo, BlockRepo blockRepo, ConversationRepo conversationRepo,
            MessageRepo messageRepo, UserRepo userRepo, SimpMessagingTemplate simp) {
        this.entityManager = entityManager;
        this.friendshipRepo = friendshipRepo;
        this.friendRequestRepo = friendRequestRepo;
        this.blockRepo = blockRepo;
        this.conversationRepo = conversationRepo;
        this.messageRepo = messageRepo;
        this.userRepo = userRepo;
        this.simp = simp;
    }

    public void sendToChannelAcceptOrDecline(String username, Long receiverId) {
        simp.convertAndSendToUser(username, FRIEND_REQUEST_ACTION_CHANNEL + ".b1", receiverId);
    }

    public void sendToChannelCancel(String username, Long senderId) {
        simp.convertAndSendToUser(username, FRIEND_REQUEST_ACTION_CHANNEL + ".b2", senderId);
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
    public FriendRequestDTO sendInvite(long senderId, long receiverId) {

        Block checkBlock = blockRepo.findByBlockedId(senderId).orElse(null);

        // If sender is being blocked by receiver
        if (checkBlock != null) {
            throw new IllegalStateException("Sender is being blocked by receiver");
        }

        FriendRequest check = friendRequestRepo.findBySenderIdAndReceiverId(receiverId, senderId).orElse(null);

        // If receiver already send an invite to sender
        if (check != null) {
            this.acceptInvite(senderId, receiverId);
            return new FriendRequestDTO();
        }

        User sender = entityManager.getReference(User.class, senderId);
        User receiver = entityManager.getReference(User.class, receiverId);

        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setCreatedAt(LocalDate.now());

        friendRequestRepo.save(request);

        FriendRequestDTO dto = new FriendRequestDTO(
                senderId,
                receiverId,
                sender.getUsername(),
                receiver.getUsername(),
                sender.getAvatarUrl(),
                receiver.getAvatarUrl());

        simp.convertAndSendToUser(receiver.getUsername(), FRIEND_INIVATIONS_CHANNEL, dto);

        return dto;

    }

    /**
     * Get all Friendship in DB that have friendId = userId with status = "Pending"
     * 
     * @param userId
     * @return
     */
    public List<FriendRequestDTO> getInviteFromFriend(Long userId) {
        List<FriendRequest> queryResult = friendRequestRepo.findAllByReceiverId(userId).orElse(null);
        return queryResult.stream()
                .map(request -> new FriendRequestDTO(
                        request.getSender().getUserId(),
                        request.getReceiver().getUserId(),
                        request.getSender().getUsername(),
                        request.getReceiver().getUsername(),
                        request.getSender().getAvatarUrl(),
                        request.getReceiver().getAvatarUrl()))
                .toList();
    }

    /**
     * Get all Friendship in DB that have userId = userid with status "Pending"
     * 
     * @param userId
     * @return
     */
    public List<FriendRequestDTO> getInviteFromUser(Long userId) {
        List<FriendRequest> queryResult = friendRequestRepo.findAllBySenderId(userId).orElse(null);
        return queryResult.stream()
                .map(request -> new FriendRequestDTO(
                        request.getSender().getUserId(),
                        request.getReceiver().getUserId(),
                        request.getSender().getUsername(),
                        request.getReceiver().getUsername(),
                        request.getSender().getAvatarUrl(),
                        request.getReceiver().getAvatarUrl()))
                .toList();
    }

    /**
     * First, delete request in FriendRequest in DB, then create a
     * new record
     * 
     * Constrains: userId1 < userId2
     * 
     * Assume: On Frontend, when current User accept Invite, the invite will
     * automatically
     * disappear.
     * 
     * @param userId
     * @param friendId
     * @return
     */
    @Transactional
    public FriendDTO acceptInvite(Long curUserId, Long senderId) {
        // Find Request
        FriendRequest request = friendRequestRepo.findBySenderIdAndReceiverId(senderId, curUserId).orElse(null);
        if (request == null) {
            throw new EntityNotFoundException("REQUEST_NOT_FOUND");
        }

        // Delete Request
        friendRequestRepo.delete(request);

        // Create new Friend
        User user1 = entityManager.getReference(User.class, (curUserId < senderId) ? curUserId : senderId);
        User user2 = entityManager.getReference(User.class, (curUserId < senderId) ? senderId : curUserId);
        FriendshipId newKey = new FriendshipId(user1.getUserId(), user2.getUserId());
        Friendship newF = new Friendship();
        newF.setFriendshipId(newKey);
        newF.setUser1(user1);
        newF.setUser2(user2);
        newF.setCreatedAt(LocalDate.now());

        // Save Friend
        friendshipRepo.save(newF);

        if (conversationRepo.findByUser1AndUser2(user1.getUserId(), user2.getUserId()) == null) {
            Conversation newConversation = new Conversation();
            newConversation.setUser1(user1);
            newConversation.setUser2(user2);
            newConversation.setCreatedAt(LocalDate.now());
            conversationRepo.save(newConversation);
        }

        User sender = entityManager.getReference(User.class, senderId);
        this.sendToChannelAcceptOrDecline(sender.getUsername(), curUserId);

        boolean isUser1 = newF.getFriendshipId().getUser1Id().equals(curUserId);
        User friendUser = isUser1 ? newF.getUser2() : newF.getUser1();

        return new FriendDTO(
                friendUser.getUserId(),
                friendUser.getUsername(),
                friendUser.getAvatarUrl());
    }

    /**
     * Delete an invite, use by Decline on User Interfrace, therefore, delete the
     * invitation
     * made by sender
     * 
     * @param curUserId
     * @param senderId
     * @return
     */
    @Transactional
    public void deleteInvite(Long senderId, Long receiverId) {

        if (receiverId == null) {
            throw new IllegalArgumentException("ReceiverID must not null");
        }
        if (senderId == null) {
            throw new IllegalArgumentException("SenderID must not null");
        }

        FriendRequest request = friendRequestRepo.findBySenderIdAndReceiverId(senderId, receiverId).orElse(null);

        if (request == null) {
            throw new EntityNotFoundException("No Friend Request found");
        }

        friendRequestRepo.delete(request);
    }

    /**
     * Same as accept invite but with status "blocked"
     * 
     * @param userId
     * @param friendId
     * @return
     */
    @Transactional
    public FriendDTO blockUser(Long curUserId, Long blockedId) {
        if (curUserId == null) {
            throw new IllegalArgumentException("CurUserID must not null");
        }
        if (blockedId == null) {
            throw new IllegalArgumentException("CurUserID must not null");
        }

        User blocker = entityManager.getReference(User.class, curUserId);
        User blocked = entityManager.getReference(User.class, blockedId);

        Block newB = new Block();
        newB.setBlocker(blocker);
        newB.setBlocked(blocked);
        newB.setCreatedAt(LocalDate.now());

        blockRepo.save(newB);

        return new FriendDTO(blockedId, blocked.getUsername(), blocked.getAvatarUrl());
    }

    /**
     * Delete a Friendship data base on userId and friendId, find both combination
     * (userId, friendId) & (friendId, userId)
     * 
     * @param userId
     * @param friendId
     */
    @Transactional
    public void unfriend(Long userId, Long friendId) {
        Friendship friendship = friendshipRepo.findByUser1AndUser2(
                (userId < friendId) ? userId : friendId, (userId < friendId) ? friendId : userId).orElse(null);

        friendshipRepo.delete(friendship);
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

    public void saveGroupMessage(ChatMessageDTO msg, String username){

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
