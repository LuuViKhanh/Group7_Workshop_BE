package org.example.flyora_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.flyora_backend.DTOs.NotificationDTO;
import org.example.flyora_backend.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "Thông báo hệ thống cho tài khoản")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * ✅ API lấy danh sách thông báo
     * 🔹 GET /api/v1/notifications?accountId=1
     * 🔸 Trả: danh sách thông báo mới
     */
    @GetMapping
    @Operation(summary = "Lấy danh sách thông báo", description = "Trả về danh sách thông báo theo account.")
    public ResponseEntity<List<NotificationDTO>> getNotifications(@RequestParam Integer accountId) {
        return ResponseEntity.ok(notificationService.getNotifications(accountId));
    }
}