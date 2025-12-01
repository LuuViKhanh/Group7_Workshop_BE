package org.example.flyora_backend.service;

import java.time.Instant;
import java.util.List;
import org.example.flyora_backend.DTOs.ChatBotDTO;
import org.example.flyora_backend.dynamo.models.ChatBotDynamoDB;
import org.example.flyora_backend.repository.ChatBotRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatBotServiceImpl implements ChatBotService {
    private final ChatBotRepository chatBotRepository;

    @Override
    public void sendMessage(ChatBotDTO dto) {
        ChatBotDynamoDB chat = new ChatBotDynamoDB();
        chat.setId(chatBotRepository.generateNewId());
        chat.setCustomerId(dto.customerId());
        chat.setMessage(dto.message());
        chat.setResponse(null);
        chat.setCreatedAt(Instant.now().toString());

        chatBotRepository.save(chat);
    }

    @Override
    public List<ChatBotDynamoDB> getMessages(Integer customerId) {
        // Repository đã implement sort
        return chatBotRepository.findByCustomerId(customerId);
    }
}