package org.example.flyora_backend.service;

import java.util.List;

import org.example.flyora_backend.DTOs.ChatBotDTO;
import org.example.flyora_backend.dynamo.models.ChatBotDynamoDB;

public interface ChatBotService {
    void sendMessage(ChatBotDTO dto);
    List<ChatBotDynamoDB> getMessages(Integer customerId);
}