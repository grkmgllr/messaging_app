package com.megatron44.howudoin.service;

import com.megatron44.howudoin.dto.FriendRequestDto;
import com.megatron44.howudoin.model.FriendRequest;
import com.megatron44.howudoin.model.FriendRequestStatus;
import com.megatron44.howudoin.model.User;
import com.megatron44.howudoin.repository.FriendRequestRepository;
import com.megatron44.howudoin.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FriendService {

    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;

    public FriendService(FriendRequestRepository friendRequestRepository, UserRepository userRepository) {
        this.friendRequestRepository = friendRequestRepository;
        this.userRepository = userRepository;
    }

    public String sendFriendRequest(String senderId, FriendRequestDto friendRequestDto) {
        if (friendRequestRepository.findBySenderIdAndReceiverId(
                senderId,
                friendRequestDto.getReceiverId()
        ).isPresent()) {
            throw new RuntimeException("Friend request already exists.");
        }

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSenderId(senderId);
        friendRequest.setReceiverId(friendRequestDto.getReceiverId());
        friendRequest.setStatus(FriendRequestStatus.PENDING.toString());

        friendRequestRepository.save(friendRequest);
        return "Friend request sent successfully.";
    }

    public String acceptFriendRequest(String friendRequestId) {
        FriendRequest friendRequest = friendRequestRepository.findById(friendRequestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found."));

        if (!FriendRequestStatus.PENDING.toString().equals(friendRequest.getStatus())) {
            throw new RuntimeException("Friend request is not pending.");
        }

        // Update friend request status to ACCEPTED
        friendRequest.setStatus(FriendRequestStatus.ACCEPTED.toString());
        friendRequestRepository.save(friendRequest);

        return "Friend request accepted successfully.";
    }

    public List<String> getFriendList(String userId) {
        // Only get friends where there is an ACCEPTED request
        List<FriendRequest> acceptedRequests = friendRequestRepository.findBySenderIdOrReceiverIdAndStatus(
                userId, userId, FriendRequestStatus.ACCEPTED.toString());
    
        List<String> friendIds = new ArrayList<>();
        for (FriendRequest request : acceptedRequests) {
            // Add the other user's ID to the friends list
            if (request.getSenderId().equals(userId)) {
                friendIds.add(request.getReceiverId());
            } else {
                friendIds.add(request.getSenderId());
            }
        }
        return friendIds;
    }

    public List<FriendRequest> getPendingRequests(String userId) {
        return friendRequestRepository.findByReceiverIdAndStatus(
            userId, 
            FriendRequestStatus.PENDING.toString()
        );
    }
}
