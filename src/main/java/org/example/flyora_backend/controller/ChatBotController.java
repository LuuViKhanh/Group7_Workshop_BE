package org.example.flyora_backend.controller;

import java.util.List;
import java.util.Map;

import org.example.flyora_backend.DTOs.ChatBotDTO;
import org.example.flyora_backend.dynamo.models.ChatBotDynamoDB;
import org.example.flyora_backend.service.ChatBotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/chatbot")
@RequiredArgsConstructor
@Tag(name = "ChatBot", description = "Tư vấn khách hàng")
public class ChatBotController {

    private final ChatBotService chatBotService;

    @PostMapping("/send")
    @Operation(summary = "Khách gửi câu hỏi")
    public ResponseEntity<?> send(@RequestBody ChatBotDTO dto) {
        chatBotService.sendMessage(dto);
        return ResponseEntity.ok(Map.of("message", "Tin nhắn của bạn đã được gửi"));
    }

    @GetMapping("/history")
    @Operation(summary = "Xem lịch sử nhắn tin")
    public ResponseEntity<List<ChatBotDynamoDB>> getHistory(@RequestParam Integer customerId) {
        return ResponseEntity.ok(chatBotService.getMessages(customerId));
    }
}
