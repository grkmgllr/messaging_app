package com.megatron44.howudoin.controller;

import com.megatron44.howudoin.dto.FriendRequestDto;
import com.megatron44.howudoin.model.FriendRequest;
import com.megatron44.howudoin.service.FriendService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
public class FriendController {

    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> sendFriendRequest(
            @RequestBody FriendRequestDto friendRequestDto,
            @AuthenticationPrincipal String senderUsername
    ) {
        String response = friendService.sendFriendRequest(senderUsername, friendRequestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/accept/{friendRequestUsername}")
    public ResponseEntity<String> acceptFriendRequest(@PathVariable String friendRequestUsername) {
        String response = friendService.acceptFriendRequest(friendRequestUsername);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<String>> getFriendList() {
        String userUsername = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> friends = friendService.getFriendList(userUsername);
        return ResponseEntity.ok(friends);
    }
    @GetMapping("/pending")
    public ResponseEntity<List<FriendRequest>> getPendingRequests(
            @AuthenticationPrincipal String userUsername
    ) {
        List<FriendRequest> pendingRequests = friendService.getPendingRequests(userUsername);
        return ResponseEntity.ok(pendingRequests);
    }
}
