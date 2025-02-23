package com.megatron44.howudoin.model;

import org.springframework.data.annotation.Transient;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "friend_requests")
public class FriendRequest {
    @Id
    private String id;
    private String senderId;
    private String receiverId;
    private String status;
    
    @Transient  // This field won't be stored in MongoDB
    private String senderUsername;
}