package com.megatron44.howudoin.repository;

import com.megatron44.howudoin.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {

    @Query("{ '$or': [ " +
            "{ 'senderId': ?0, 'receiverId': ?1 }, " +
            "{ 'senderId': ?1, 'receiverId': ?0 } " +
            "] }")
    List<Message> findConversation(String userId1, String userId2);

    List<Message> findBySenderIdOrReceiverId(String senderId, String receiverId);
}
