package com.megatron44.howudoin.controller;

import com.megatron44.howudoin.dto.MessageSendDto;
import com.megatron44.howudoin.model.Message;
import com.megatron44.howudoin.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody MessageSendDto messageSendDto) {
        // Extract the sender's ID from the SecurityContext
        String senderId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.debug("Sender ID from SecurityContext: {}", senderId);

        messageService.sendMessage(senderId, messageSendDto);
        return ResponseEntity.ok("Message sent successfully.");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Message>> getConversation(@PathVariable String userId) {
        // Extract the current user's ID from the SecurityContext
        String currentUserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.debug("Fetching conversation between user {} and user {}", currentUserId, userId);

        List<Message> conversation = messageService.getConversation(currentUserId, userId);
        if (conversation.isEmpty()) {
            logger.info("No messages found between user {} and user {}", currentUserId, userId);
            return ResponseEntity.noContent().build(); // Return 204 if no messages found
        }

        return ResponseEntity.ok(conversation);
    }

    @GetMapping
    public ResponseEntity<List<Message>> getAllMessages() {
        String currentUserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.debug("Fetching all messages for user {}", currentUserId);

        List<Message> messages = messageService.getAllUserMessages(currentUserId);
        if (messages.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(messages);
    }

}
