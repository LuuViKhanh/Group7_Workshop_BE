package org.example.flyora_backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.flyora_backend.DTOs.WebhookURL;
import org.example.flyora_backend.service.PayOSService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payos")
@RequiredArgsConstructor
public class PayOsController {

    private final PayOSService payOSService;

    @PostMapping("/create-link/{orderId}")
    public ResponseEntity<Object> createPaymentLink(@PathVariable int orderId,
            @RequestParam(required = false) Integer amount) {
        Object paymentLink = payOSService.createPaymentLink(orderId, amount != null ? amount : 0);
        return ResponseEntity.ok(paymentLink);
    }

    @PostMapping("/confirm-webhook")
    public ResponseEntity<String> confirmWebhook(@RequestBody WebhookURL body) {
        String result = payOSService.confirmWebhook(body);
        return ResponseEntity.ok(result);
    }

}
