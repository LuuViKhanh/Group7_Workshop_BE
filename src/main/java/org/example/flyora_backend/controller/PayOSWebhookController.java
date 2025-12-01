package org.example.flyora_backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.flyora_backend.DTOs.WebhookType;
import org.example.flyora_backend.service.PayOSService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/payos") // Gốc: /webhook
@RequiredArgsConstructor
public class PayOSWebhookController {

    private final PayOSService payOSService;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody WebhookType webhookData) {
        payOSService.handlePaymentWebhook(webhookData);
        return ResponseEntity.ok("✅ Webhook received");
    }
}
