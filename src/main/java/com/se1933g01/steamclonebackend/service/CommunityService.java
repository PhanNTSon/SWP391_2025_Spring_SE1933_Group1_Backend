package com.se1933g01.steamclonebackend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.se1933g01.steamclonebackend.dto.community.BlockDTO;
import com.se1933g01.steamclonebackend.dto.community.ConversationDTO;
import com.se1933g01.steamclonebackend.dto.community.CreateGroupChatDTO;
import com.se1933g01.steamclonebackend.dto.community.FriendRequestDTO;
import com.se1933g01.steamclonebackend.dto.community.GroupChatDTO;
import com.se1933g01.steamclonebackend.dto.community.GroupChatMessageDTO;
import com.se1933g01.steamclonebackend.dto.community.GroupDTO;
import com.se1933g01.steamclonebackend.dto.community.GroupMemberDTO;
import com.se1933g01.steamclonebackend.dto.community.MessageDTO;
import com.se1933g01.steamclonebackend.dto.community.PrivateChatMessageDTO;
import com.se1933g01.steamclonebackend.dto.community.SearchResult;
import com.se1933g01.steamclonebackend.dto.user.FriendDTO;
import com.se1933g01.steamclonebackend.entity.community.Block;
import com.se1933g01.steamclonebackend.entity.community.BlockId;
import com.se1933g01.steamclonebackend.entity.community.Conversation;
import com.se1933g01.steamclonebackend.entity.community.FriendRequest;
import com.se1933g01.steamclonebackend.entity.community.Friendship;
import com.se1933g01.steamclonebackend.entity.community.FriendshipId;
import com.se1933g01.steamclonebackend.entity.community.GroupChat;
import com.se1933g01.steamclonebackend.entity.community.GroupChatMember;
import com.se1933g01.steamclonebackend.entity.community.GroupChatMemberId;
import com.se1933g01.steamclonebackend.entity.community.GroupMessage;
import com.se1933g01.steamclonebackend.entity.community.Message;
import com.se1933g01.steamclonebackend.entity.user.User;
import com.se1933g01.steamclonebackend.repository.BlockRepo;
import com.se1933g01.steamclonebackend.repository.ConversationRepo;
import com.se1933g01.steamclonebackend.repository.FriendRequestRepo;
import com.se1933g01.steamclonebackend.repository.FriendshipRepo;
import com.se1933g01.steamclonebackend.repository.GroupChatMemberRepo;
import com.se1933g01.steamclonebackend.repository.GroupChatRepo;
import com.se1933g01.steamclonebackend.repository.GroupMessageRepo;
import com.se1933g01.steamclonebackend.repository.MessageRepo;
import com.se1933g01.steamclonebackend.repository.UserRepo;
import com.se1933g01.steamclonebackend.utils.GroupAvatarGenerator;

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
    private final GroupMessageRepo groupMessageRepo;
    private final GroupChatMemberRepo gcmRepo;
    private final GroupChatRepo groupChatRepo;

    private final static String FRIEND_INIVATIONS_CHANNEL = "/queue/friend.invitations";
    private final static String FRIEND_REQUEST_ACTION_CHANNEL = "/queue/friend.request";

    // server broadcasts these
    private static final String FRIEND_ADDED_CHANNEL = "/topic/friends.%d.added";
    private static final String FRIEND_REMOVED_CHANNEL = "/topic/friends.%d.removed";

    private static final String BLOCKED_ADDED_CHANNEL = "/topic/blocks.%d.added";
    private static final String BLOCKED_REMOVED_CHANNEL = "/topic/blocks.%d.removed";

    private static final String GROUP_ADDED_CHANNEL = "/topic/groups.%d.added";
    private static final String GROUP_REMOVED_CHANNEL = "/topic/groups.%d.removed";

    private static final String GROUP_JOIN_CHANNEL = "/topic/group.%d.join";
    private static final String GROUP_LEAVE_CHANNEL = "/topic/group.%d.leave";

    public CommunityService(EntityManager entityManager, FriendshipRepo friendshipRepo,
            FriendRequestRepo friendRequestRepo, BlockRepo blockRepo, ConversationRepo conversationRepo,
            MessageRepo messageRepo, UserRepo userRepo, SimpMessagingTemplate simp, GroupMessageRepo groupMessageRepo,
            GroupChatMemberRepo gcmRepo, GroupChatRepo groupChatRepo) {
        this.entityManager = entityManager;
        this.friendshipRepo = friendshipRepo;
        this.friendRequestRepo = friendRequestRepo;
        this.blockRepo = blockRepo;
        this.conversationRepo = conversationRepo;
        this.messageRepo = messageRepo;
        this.userRepo = userRepo;
        this.simp = simp;
        this.groupMessageRepo = groupMessageRepo;
        this.gcmRepo = gcmRepo;
        this.groupChatRepo = groupChatRepo;
    }

    public void sendToChannelAcceptOrDecline(String username, Long receiverId) {
        simp.convertAndSendToUser(username, FRIEND_REQUEST_ACTION_CHANNEL + ".b1", receiverId);
    }

    public void sendToChannelCancel(String username, Long senderId) {
        simp.convertAndSendToUser(username, FRIEND_REQUEST_ACTION_CHANNEL + ".b2", senderId);
    }

    private void sendFriendAdded(Long receiverId, FriendDTO friendDto) {
        String dest = String.format(FRIEND_ADDED_CHANNEL, receiverId);
        simp.convertAndSend(dest, friendDto);
    }

    private void sendFriendRemoved(Long receiverId, Long removedFriendId) {
        String dest = String.format(FRIEND_REMOVED_CHANNEL, receiverId);
        simp.convertAndSend(dest, removedFriendId);
    }

    private void sendBlockedAdded(Long receiverId, BlockDTO blockDTO) {
        String dest = String.format(BLOCKED_ADDED_CHANNEL, receiverId);
        simp.convertAndSend(dest, blockDTO);
    }

    /**
     * Send to Current User the ID of User they unban
     * Or Send to Other User the ID of the Person that have unbaned them.
     * 
     * @param receiverId
     * @param removedFriendId
     */
    private void sendBlockedRemoved(Long receiverId, Long removeId) {
        String dest = String.format(BLOCKED_REMOVED_CHANNEL, receiverId);
        simp.convertAndSend(dest, removeId);
    }

    private void sendGroupAdded(Long receiverId, GroupDTO groupDTO) {
        String dest = String.format(GROUP_ADDED_CHANNEL, receiverId);
        simp.convertAndSend(dest, groupDTO);
    }

    private void sendGroupRemoved(Long receiverId, Long removedGroupId) {
        String dest = String.format(GROUP_REMOVED_CHANNEL, receiverId);
        simp.convertAndSend(dest, removedGroupId);
    }

    private void sendGroupJoin(Long groupId, GroupMemberDTO groupMemberDTO) {
        String dest = String.format(GROUP_JOIN_CHANNEL, groupId);
        simp.convertAndSend(dest, groupMemberDTO);
    }

    private void sendGroupLeave(Long groupId, Long leaveMemberId) {
        String dest = String.format(GROUP_LEAVE_CHANNEL, groupId);
        simp.convertAndSend(dest, leaveMemberId);
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

        Block checkBlock = blockRepo.findByBlockerIdAndBlockedId(senderId, receiverId).orElse(null);

        // If sender is being blocked by receiver
        if (checkBlock != null) {
            throw new IllegalStateException("Sender is being blocked by receiver or reverse");
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

        boolean isUser1 = newF.getFriendshipId().getUser1Id().equals(curUserId);
        User friendUser = isUser1 ? newF.getUser2() : newF.getUser1();
        User curUser = isUser1 ? newF.getUser1() : newF.getUser2();

        FriendDTO dto = new FriendDTO(
                friendUser.getUserId(),
                friendUser.getUsername(),
                friendUser.getAvatarUrl(),
                friendUser.getGroupChatList().size() + friendUser.getGroupMemberships().size());

        User sender = entityManager.getReference(User.class, senderId);
        this.sendToChannelAcceptOrDecline(sender.getUsername(), curUserId);

        // send to current user
        this.sendFriendAdded(curUserId, dto);

        // send to the other party
        sendFriendAdded(senderId, new FriendDTO(
                curUser.getUserId(),
                curUser.getUsername(),
                curUser.getAvatarUrl(),
                curUser.getGroupChatList().size() + curUser.getGroupMemberships().size()));

        return dto;
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
    public BlockDTO blockUser(Long curUserId, Long blockedId) {
        if (curUserId == null) {
            throw new IllegalArgumentException("CurUserID must not null");
        }
        if (blockedId == null) {
            throw new IllegalArgumentException("CurUserID must not null");
        }

        Friendship checkFriendship = friendshipRepo.findByUser1AndUser2(curUserId < blockedId ? curUserId : blockedId,
                curUserId < blockedId ? blockedId : curUserId)
                .orElse(null);

        if (checkFriendship != null)
            throw new IllegalStateException("Current User is friend of the other User");

        Block checkBlock = blockRepo.findByBlockerIdAndBlockedId(curUserId, blockedId).orElse(null);
        if (checkBlock != null) {
            throw new IllegalStateException("Current User are already being blocked by other User");
        }

        User blocker = entityManager.getReference(User.class, curUserId);
        User blocked = entityManager.getReference(User.class, blockedId);

        Block newB = new Block();
        BlockId newId = new BlockId(curUserId, blockedId);
        newB.setBlockId(newId);
        newB.setBlocker(blocker);
        newB.setBlocked(blocked);
        newB.setCreatedAt(LocalDate.now());

        blockRepo.save(newB);

        BlockDTO dto = new BlockDTO(curUserId, blocker.getUsername(), blocker.getAvatarUrl(), blocked.getUserId(),
                blocked.getUsername(), blocked.getAvatarUrl());

        this.sendBlockedAdded(curUserId, dto);
        this.sendBlockedAdded(blockedId, dto);

        return dto;
    }

    /**
     * Unblock somebody, only made by the blocker side
     * 
     * @param curUserId
     * @param otherId
     */
    @Transactional
    public void unBlocked(Long curUserId, Long otherId) {
        BlockId curBlockId = new BlockId(curUserId, otherId);
        Block curBlock = entityManager.getReference(Block.class, curBlockId);

        blockRepo.delete(curBlock);

        this.sendBlockedRemoved(curUserId, otherId);
        this.sendBlockedRemoved(otherId, curUserId);

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
                (userId < friendId) ? userId : friendId, (userId < friendId) ? friendId : userId)
                .orElseThrow(() -> new IllegalStateException("No friendships between Current User and other user"));

        friendshipRepo.delete(friendship);
        this.sendFriendRemoved(userId, friendId);
        this.sendFriendRemoved(friendId, userId);

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
                        message.getSender().getUserId(),
                        message.getSender().getUsername(),
                        message.getMessageContent(),
                        message.getSentAt())).toList());
    }

    public GroupChatDTO getGroupChat(Long groupId) {
        // Get messages
        List<GroupMessage> queryRes = groupMessageRepo.findAllByGroupChatId(groupId).orElse(null);
        List<MessageDTO> messages = queryRes.stream()
                .map(m -> new MessageDTO(
                        m.getSender().getUserId(),
                        m.getSender().getUsername(),
                        m.getMessage(), m.getSentAt()))
                .toList();

        // Get Members
        List<GroupChatMember> members = gcmRepo.findAllMembersByGroupId(groupId)
                .orElse(Collections.emptyList());
        List<GroupMemberDTO> friends = members.stream()
                .map(member -> new GroupMemberDTO(
                        member.getMember().getUserId(),
                        member.getMember().getUsername(),
                        member.isAdmin(),
                        member.getMember().getAvatarUrl()))
                .toList();

        return new GroupChatDTO(friends, messages);
    }

    @Transactional
    public void saveGroupMessage(MessageDTO msg, Long groupId) {
        GroupMessage nMess = new GroupMessage();

        GroupChat group = entityManager.getReference(GroupChat.class, groupId);
        User sender = entityManager.getReference(User.class, msg.getSenderId());

        nMess.setGroup(group);
        nMess.setSender(sender);
        nMess.setMessage(msg.getMessageContent());
        nMess.setSentAt(msg.getSentAt());

        groupMessageRepo.save(nMess);
    }

    @Transactional
    public void saveMessage(PrivateChatMessageDTO msg, String username) {
        Message nMessage = new Message();
        Conversation conver = entityManager.getReference(Conversation.class, msg.getConversationId());
        User sender = userRepo.findByUsername(username).orElse(null);
        if (sender.isBanStatus()) {
            throw new IllegalStateException("[User] Current sender is being Banned");
        }

        nMessage.setConversation(conver);
        nMessage.setSender(sender);
        nMessage.setMessageContent(msg.getContent());
        nMessage.setSentAt(msg.getSentAt());

        messageRepo.save(nMessage);
    }

    @Transactional
    public void saveGroupMessage(GroupChatMessageDTO msg) {
        GroupChat group = entityManager.getReference(GroupChat.class, msg.getGroupId());
        User sender = entityManager.getReference(User.class, msg.getSenderId());
        GroupMessage nMessage = new GroupMessage();
        nMessage.setGroup(group);
        nMessage.setSender(sender);
        nMessage.setMessage(msg.getContent());
        nMessage.setSentAt(msg.getSentAt());

        groupMessageRepo.save(nMessage);

    }

    public SearchResult findUser(long friendId) throws Exception {
        User target = userRepo.findById(friendId).orElse(null);
        if (target == null) {
            throw new Exception("No User found");
        } else {
            return new SearchResult(target.getUserId(), target.getUsername(), target.getAvatarUrl());
        }
    }

    @Transactional
    public GroupDTO createGroupChat(CreateGroupChatDTO newG, Long ownerId) {
        if (newG == null) {
            throw new IllegalArgumentException("New Group Details must not be Null");
        }
        if (ownerId == null) {
            throw new IllegalArgumentException("OwnerID must not be Null");
        }

        User owner = entityManager.getReference(User.class, ownerId);

        if (owner.getGroupMemberships().size() >= 10) {
            throw new IllegalStateException("[Creater] Exceed limitation number of Groups");
        }

        GroupChat newGroup = new GroupChat();
        newGroup.setGroupName(newG.getGroupName());
        newGroup.setOwner(owner);
        newGroup.setCreatedAt(LocalDateTime.now());

        GroupChat saved = groupChatRepo.save(newGroup);

        // Saved owner to DB
        GroupChatMemberId oId = new GroupChatMemberId(saved.getGroupId(), ownerId);
        GroupChatMember o = new GroupChatMember(oId, saved, owner, true, LocalDateTime.now());
        gcmRepo.save(o);

        // Saved members to DB
        newG.getMembers().stream().forEach(member -> {
            if (member.getMemberId() == null) {
                throw new IllegalArgumentException("MemberID must not be Null in Create new Group");
            }
            User m = entityManager.getReference(User.class, member.getMemberId());

            if (m.getGroupMemberships().size() >= 10) {
                throw new IllegalStateException("[Member] Exceed limitations number of Groups take part in");
            }

            GroupChatMemberId id = new GroupChatMemberId(saved.getGroupId(), member.getMemberId());
            GroupChatMember newMember = new GroupChatMember();
            newMember.setId(id);
            newMember.setMember(m);
            newMember.setAdmin(false);
            newMember.setGroup(newGroup);
            newMember.setJoinedAt(LocalDateTime.now());

            gcmRepo.save(newMember);
        });

        GroupDTO dto = new GroupDTO(saved.getGroupId(), saved.getGroupName(),
                GroupAvatarGenerator.generateGroupAvatar(
                        newG.getMembers().stream().map(member -> member.getMemberAvatar()).toList()));

        this.sendGroupAdded(ownerId, dto);
        newG.getMembers().forEach(member -> this.sendGroupAdded(member.getMemberId(), dto));

        return dto;
    }

    @Transactional
    public void deleteGroupChat(Long groupId, Long userId) {

        if (groupId == null) {
            throw new IllegalArgumentException("GroupID must not be null");
        }

        GroupChat gc = entityManager.getReference(GroupChat.class, groupId);

        if (gc.getOwner().getUserId() != userId) {
            throw new IllegalArgumentException("UserID does not match with OwnerID of current Group");
        }

        groupChatRepo.delete(gc);

        this.sendGroupRemoved(gc.getOwner().getUserId(), groupId);
        gc.getMembers().stream().forEach(member -> this.sendGroupRemoved(member.getMember().getUserId(), groupId));

    }

    @Transactional
    public List<GroupMemberDTO> joinGroup(Long groupId, List<GroupMemberDTO> newMembers) {

        GroupChat group = entityManager.getReference(GroupChat.class, groupId);

        newMembers.stream().forEach(member -> {
            if (member.getMemberId() == null) {
                throw new IllegalArgumentException("MemberID must not be Null in Joining to Group");
            }
            User m = entityManager.getReference(User.class, member.getMemberId());

            if (m.getGroupMemberships().size() >= 10) {
                throw new IllegalStateException("[Member] Exceed limitations number of Groups take part in");
            }

            GroupChatMemberId id = new GroupChatMemberId(groupId, member.getMemberId());
            GroupChatMember newMember = new GroupChatMember();
            newMember.setId(id);
            newMember.setMember(m);
            newMember.setAdmin(false);
            newMember.setGroup(group);
            newMember.setJoinedAt(LocalDateTime.now());

            gcmRepo.save(newMember);

            this.sendGroupJoin(groupId, member);

            this.sendGroupAdded(member.getMemberId(),
                    new GroupDTO(groupId, group.getGroupName(), GroupAvatarGenerator.generateGroupAvatar(group)));
        });

        return newMembers;
    }

    @Transactional
    public void leaveGroup(Long groupId, Long memberId) {
        GroupChatMember member = gcmRepo.findByMemberIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group member not found"));
        gcmRepo.delete(member);
        this.sendGroupRemoved(memberId, groupId);
        this.sendGroupLeave(groupId, memberId);
    }

    @Transactional
    public void kickMembersInGroup(Long groupId, List<Long> kickMemberIds) {
        kickMemberIds.stream().forEach(id -> {
            GroupChatMemberId dbId = new GroupChatMemberId(groupId, id);
            GroupChatMember ref = entityManager.getReference(GroupChatMember.class, dbId);

            gcmRepo.delete(ref);
            this.sendGroupLeave(groupId, id);
        });
    }
}
