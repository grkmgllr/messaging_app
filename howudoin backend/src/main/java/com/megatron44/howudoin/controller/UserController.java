package com.megatron44.howudoin.controller;

import com.megatron44.howudoin.model.User;
import com.megatron44.howudoin.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserRepository userRepository;
    
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable String userId) {
        return userRepository.findById(userId)
            .map(user -> ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername()
            )))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{userId}/friends")
    public ResponseEntity<?> getUserFriends(@PathVariable String userId) {
        return userRepository.findById(userId)
            .map(user -> {
                List<Map<String, String>> friendsList = user.getFriends().stream()
                    .map(friendId -> userRepository.findById(friendId))
                    .filter(Optional::isPresent)
                    .map(friend -> Map.of(
                        "id", friend.get().getId(),
                        "username", friend.get().getUsername()
                    ))
                    .collect(Collectors.toList());
                
                return ResponseEntity.ok(friendsList);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search/friends")
    public ResponseEntity<?> searchFriends(@RequestParam String username) {
        String currentUserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        return userRepository.findById(currentUserId)
            .map(currentUser -> {
                List<Map<String, String>> matchingFriends = currentUser.getFriends().stream()
                    .map(friendId -> userRepository.findById(friendId))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(friend -> friend.getUsername().toLowerCase().contains(username.toLowerCase()))
                    .map(friend -> Map.of(
                        "id", friend.getId(),
                        "username", friend.getUsername()
                    ))
                    .collect(Collectors.toList());
                
                return ResponseEntity.ok(matchingFriends);
            })
            .orElse(ResponseEntity.notFound().build());
    }
} 