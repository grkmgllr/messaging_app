package com.megatron44.howudoin.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String username;
    private String email;
    private String password; // Store hashed passwords
    private List<String> friends = new ArrayList<>(); // List of user IDs representing the user's friends
    private List<Message> messages; // Embedded messages
}

