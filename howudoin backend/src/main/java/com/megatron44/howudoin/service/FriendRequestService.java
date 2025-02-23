package com.megatron44.howudoin.service;

import com.megatron44.howudoin.exception.ForbiddenException;
import com.megatron44.howudoin.model.FriendRequest;
import com.megatron44.howudoin.model.FriendRequestStatus;
import com.megatron44.howudoin.model.User;
import com.megatron44.howudoin.repository.FriendRequestRepository;
import com.megatron44.howudoin.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;

@Service
@Slf4j
public class FriendRequestService {
    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;

    public FriendRequestService(FriendRequestRepository friendRequestRepository, UserRepository userRepository) {
        this.friendRequestRepository = friendRequestRepository;
        this.userRepository = userRepository;
    }

    public List<FriendRequest> findByReceiverIdAndStatus(String receiverId, String status) {
        log.info("Fetching friend requests for receiver: {} with status: {}", receiverId, status);
        List<FriendRequest> requests = friendRequestRepository.findByReceiverIdAndStatus(receiverId, status);
        
        // Fetch and set usernames for each request
        requests.forEach(request -> {
            userRepository.findById(request.getSenderId()).ifPresent(sender -> {
                request.setSenderUsername(sender.getUsername());
            });
        });
        
        log.info("Found {} friend requests", requests.size());
        return requests;
    }

    @Transactional
    public FriendRequest createFriendRequest(String senderId, String receiverUsername) {
        log.info("Creating friend request from sender ID: {} to username: {}", senderId, receiverUsername);
        
        // Find sender by ID (since we get ID from JWT)
        User sender = userRepository.findById(senderId)
            .orElseThrow(() -> new IllegalStateException("Sender not found"));

        // Find receiver by username
        User receiver = userRepository.findByUsername(receiverUsername)
            .orElseThrow(() -> new IllegalStateException("Receiver not found"));

        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalStateException("Cannot send friend request to yourself");
        }

        // Check if request already exists in either direction
        if (friendRequestRepository.findBySenderIdAndReceiverId(sender.getId(), receiver.getId()).isPresent() ||
            friendRequestRepository.findBySenderIdAndReceiverId(receiver.getId(), sender.getId()).isPresent()) {
            throw new IllegalStateException("Friend request already exists");
        }

        FriendRequest request = new FriendRequest();
        request.setSenderId(sender.getId());
        request.setReceiverId(receiver.getId());
        request.setStatus(FriendRequestStatus.PENDING.toString());

        return friendRequestRepository.save(request);
    }

    @Transactional
    public void acceptFriendRequest(String requestId, String userId) {
        log.info("Accepting friend request: {} by user: {}", requestId, userId);
        
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalStateException("Friend request not found"));

        if (!request.getReceiverId().equals(userId)) {
            throw new ForbiddenException("You are not authorized to accept this request");
        }

        if (!request.getStatus().equals(FriendRequestStatus.PENDING.toString())) {
            throw new IllegalStateException("Request is not in PENDING state");
        }

        // Update friend request status
        request.setStatus(FriendRequestStatus.ACCEPTED.toString());
        friendRequestRepository.save(request);

        // Update both users' friends lists
        User sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new IllegalStateException("Sender not found"));
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new IllegalStateException("Receiver not found"));

        // Initialize friends lists if null
        if (sender.getFriends() == null) {
            sender.setFriends(new ArrayList<>());
        }
        if (receiver.getFriends() == null) {
            receiver.setFriends(new ArrayList<>());
        }

        // Add each user to the other's friends list
        sender.getFriends().add(receiver.getId());
        receiver.getFriends().add(sender.getId());

        userRepository.save(sender);
        userRepository.save(receiver);

        log.info("Friend request accepted and friends lists updated successfully");
    }
}