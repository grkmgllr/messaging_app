package com.megatron44.howudoin.repository;

import com.megatron44.howudoin.model.FriendRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends MongoRepository<FriendRequest, String> {
    List<FriendRequest> findByReceiverIdAndStatus(String receiverId, String status);
    Optional<FriendRequest> findBySenderIdAndReceiverId(String senderId, String receiverId);
    
    // Updated query to properly find requests by status
    @Query("{'$or': [{'senderId': ?0}, {'receiverId': ?1}], 'status': ?2}")
    List<FriendRequest> findBySenderIdOrReceiverIdAndStatus(String senderId, String receiverId, String status);
}


