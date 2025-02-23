package com.megatron44.howudoin.controller;

import com.megatron44.howudoin.dto.CreateGroupRequest;
import com.megatron44.howudoin.dto.SendGroupMessageRequest;
import com.megatron44.howudoin.model.Group;
import com.megatron44.howudoin.service.GroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping("/create")
    public ResponseEntity<Group> createGroup(@RequestBody CreateGroupRequest request) {
        String ownerId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Group group = groupService.createGroup(ownerId, request.getName(), request.getMembers());
        return ResponseEntity.ok(group);
    }

    @PostMapping("/{groupId}/add-member/{memberId}")
    public ResponseEntity<Map<String, String>> addMember(@PathVariable String groupId, @PathVariable String memberId) {
        groupService.addMember(groupId, memberId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Member added successfully.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{groupId}/send")
    public ResponseEntity<String> sendGroupMessage(@PathVariable String groupId, @RequestBody SendGroupMessageRequest request) {
        String senderId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        groupService.sendGroupMessage(groupId, senderId, request.getContent());
        return ResponseEntity.ok("Message sent to the group.");
    }

    @GetMapping("/{groupId}/messages")
    public ResponseEntity<List<Group.Message>> getGroupMessages(@PathVariable String groupId) {
        List<Group.Message> messages = groupService.getGroupMessages(groupId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<String>> getGroupMembers(@PathVariable String groupId) {
        List<String> members = groupService.getGroupMembers(groupId);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/all_groups")
        public ResponseEntity<List<Group>> getUserGroups() {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Group> groups = groupService.getUserGroups(userId);
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<Group> getGroupInfo(@PathVariable String groupId) {
        String currentUserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Group group = groupService.getGroup(groupId);
        
        if (!group.getMembers().contains(currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(group);
    }
}
