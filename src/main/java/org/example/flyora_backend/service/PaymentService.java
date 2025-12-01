package org.example.flyora_backend.service;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PayOSService payOSService;

    public Map<String, String> createPayOSPayment(int orderId, int amount) {
        return payOSService.createPaymentLink(orderId, amount);
    }
}
