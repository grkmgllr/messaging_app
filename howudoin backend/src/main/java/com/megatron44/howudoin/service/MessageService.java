package com.megatron44.howudoin.service;

import com.megatron44.howudoin.dto.MessageSendDto;
import com.megatron44.howudoin.model.Message;
import com.megatron44.howudoin.model.User;
import com.megatron44.howudoin.repository.MessageRepository;
import com.megatron44.howudoin.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.List;

@Service
public class MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public String sendMessage(String senderId, MessageSendDto messageSendDto) {
        // Validate sender
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found."));

        // Validate receiver
        userRepository.findById(messageSendDto.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found."));

        // Validate friendship
        if (!sender.getFriends().contains(messageSendDto.getReceiverId())) {
            throw new RuntimeException("You can only send messages to friends.");
        }

        // Create and save the message
        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(messageSendDto.getReceiverId());
        message.setContent(messageSendDto.getContent());
        message.setTimestamp(new Date()); // Set the current timestamp
        messageRepository.save(message);

        logger.debug("Message sent successfully from user {} to user {}", senderId, messageSendDto.getReceiverId());
        return "Message sent successfully.";
    }

    public List<Message> getConversation(String userId1, String userId2) {
        logger.debug("Fetching conversation between user {} and user {}", userId1, userId2);

        // Validate users and friendship
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new RuntimeException("User 1 not found."));
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new RuntimeException("User 2 not found."));
        
        // Validate that both users exist in each other's friend lists
        if (!user1.getFriends().contains(userId2) || !user2.getFriends().contains(userId1)) {
            throw new RuntimeException("Users are not friends.");
        }

        try {
            // Fetch messages from repository
            List<Message> conversation = messageRepository.findConversation(userId1, userId2);

            // Sort messages by timestamp
            conversation.sort((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()));

            if (conversation.isEmpty()) {
                logger.info("No messages found between user {} and user {}", userId1, userId2);
            } else {
                logger.debug("Found {} messages between user {} and user {}", 
                        conversation.size(), userId1, userId2);
            }

            return conversation;
        } catch (Exception e) {
            logger.error("Error fetching conversation between users: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch conversation: " + e.getMessage());
        }
    }

    public List<Message> getAllUserMessages(String userId) {
        logger.debug("Fetching all messages for user {}", userId);

        try {
            // Find all messages where the user is either sender or receiver
            List<Message> allMessages = messageRepository.findBySenderIdOrReceiverId(userId, userId);

            // Sort messages by timestamp in descending order (newest first)
            allMessages.sort((m1, m2) -> m2.getTimestamp().compareTo(m1.getTimestamp()));

            logger.debug("Found {} messages for user {}", allMessages.size(), userId);
            return allMessages;
        } catch (Exception e) {
            logger.error("Error fetching messages for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to fetch messages: " + e.getMessage());
        }
    }

}
