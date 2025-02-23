package com.megatron44.howudoin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.megatron44.howudoin.service.FriendRequestService;
import com.megatron44.howudoin.exception.ForbiddenException;
import com.megatron44.howudoin.dto.FriendRequestDto;
import com.megatron44.howudoin.model.FriendRequest;
import com.megatron44.howudoin.model.FriendRequestStatus;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/friend-requests")
public class FriendRequestController {
    private final FriendRequestService friendRequestService;

    public FriendRequestController(FriendRequestService friendRequestService) {
        this.friendRequestService = friendRequestService;
    }

    @PostMapping
    public ResponseEntity<?> createFriendRequest(
            @RequestBody FriendRequestDto friendRequestDto,
            @AuthenticationPrincipal String username
    ) {
        try {
            friendRequestService.createFriendRequest(username, friendRequestDto.getReceiverUsername());
            return ResponseEntity.ok().body(Map.of("message", "Friend request sent successfully"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create friend request: " + e.getMessage()));
        }
    }

    @PutMapping("/{requestId}/accept")
    public ResponseEntity<?> acceptFriendRequest(
            @PathVariable String requestId,
            @AuthenticationPrincipal String username
    ) {
        try {
            friendRequestService.acceptFriendRequest(requestId, username);
            return ResponseEntity.ok().body(Map.of("message", "Friend request accepted successfully"));
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to accept friend request: " + e.getMessage()));
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<FriendRequest>> getPendingRequests(
            @AuthenticationPrincipal String username
    ) {
        try {
            List<FriendRequest> pendingRequests = friendRequestService
                .findByReceiverIdAndStatus(username, FriendRequestStatus.PENDING.toString());
            return ResponseEntity.ok(pendingRequests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
