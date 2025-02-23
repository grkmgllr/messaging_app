package com.megatron44.howudoin.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Document(collection = "groups")
public class Group {
    @Id
    private String id;
    private String name; // Group name
    private String ownerId; // User ID of the creator
    private List<String> members = new ArrayList<>(); // List of user IDs in the group
    private List<Message> messages = new ArrayList<>(); // Embedded messages in the group
    private LocalDateTime createdAt;  // Add this field

    public Group(String name, String ownerId, List<String> members) {
        this.name = name;
        this.ownerId = ownerId;
        this.members = members;
        this.createdAt = LocalDateTime.now();  // Set creation time
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Data
    public static class Message {
        private String senderId; // User ID of the sender
        private String content; // The actual message content
        private Date timestamp; // When the message was sent
    }
}

