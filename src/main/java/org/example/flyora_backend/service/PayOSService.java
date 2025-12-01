package org.example.flyora_backend.service;

import java.util.Map;

import org.example.flyora_backend.DTOs.WebhookType;
import org.example.flyora_backend.DTOs.WebhookURL;

public interface PayOSService {
    Map<String, String> createPaymentLink(int orderId, int amount);
    void handlePaymentWebhook(WebhookType webhookData);
    String confirmWebhook(WebhookURL body);
    
}