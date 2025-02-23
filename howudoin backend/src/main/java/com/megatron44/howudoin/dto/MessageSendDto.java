package com.megatron44.howudoin.dto;

import lombok.Data;

@Data
public class MessageSendDto {
    private String receiverId;
    private String content;
}
