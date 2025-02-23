package com.megatron44.howudoin.dto;

import lombok.Data;

@Data
public class FriendRequestDto {
    private String receiverUsername;
    private String receiverId;

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
}
