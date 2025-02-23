package com.megatron44.howudoin.service;

import com.megatron44.howudoin.model.Group;
import com.megatron44.howudoin.repository.GroupRepository;
import com.megatron44.howudoin.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public GroupService(GroupRepository groupRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    public Group createGroup(String ownerId, String groupName, List<String> members) {
        // Validate owner
        if (!userRepository.existsById(ownerId)) {
            throw new RuntimeException("Owner not found.");
        }

        // Validate members
        for (String memberId : members) {
            if (!userRepository.existsById(memberId)) {
                throw new RuntimeException("Member with ID " + memberId + " not found.");
            }
        }

        // Create group
        Group group = new Group(groupName, ownerId, members);
        group.setCreatedAt(LocalDateTime.now());

        // Create a new list with owner and members
        List<String> allMembers = new ArrayList<>();
        allMembers.add(ownerId);  // Add owner first
        allMembers.addAll(members);  // Add other members
        group.setMembers(allMembers);

        groupRepository.save(group);
        return group;
    }

    public Group addMember(String groupId, String memberId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found."));

        String currentUserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!group.getOwnerId().equals(currentUserId)) {
            throw new RuntimeException("Only the group owner can add members.");
        }

        if (!userRepository.existsById(memberId)) {
            throw new RuntimeException("Member not found.");
        }

        if (!group.getMembers().contains(memberId)) {
            group.getMembers().add(memberId);
            groupRepository.save(group);
        }

        return group;  // Return the updated group
    }


    public void sendGroupMessage(String groupId, String senderId, String content) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found."));

        if (!group.getMembers().contains(senderId)) {
            throw new RuntimeException("You are not a member of this group.");
        }

        Group.Message message = new Group.Message();
        message.setSenderId(senderId);
        message.setContent(content);
        message.setTimestamp(new Date());

        group.getMessages().add(message);
        groupRepository.save(group);
    }

    public List<Group.Message> getGroupMessages(String groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found."));

        return group.getMessages();
    }

    public List<String> getGroupMembers(String groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found."));

        return group.getMembers();
    }

    public List<Group> getUserGroups(String userId) {
        // Find all groups where the user is a member
        return groupRepository.findByMembersContaining(userId);
    }

    public Group getGroup(String groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found."));
    }
}
